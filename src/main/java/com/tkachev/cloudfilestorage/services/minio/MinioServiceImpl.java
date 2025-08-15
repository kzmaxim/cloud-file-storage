package com.tkachev.cloudfilestorage.services.minio;

import com.tkachev.cloudfilestorage.dto.FrontResourceDTO;
import com.tkachev.cloudfilestorage.dto.MinioObjectDTO;
import com.tkachev.cloudfilestorage.exceptions.*;
import com.tkachev.cloudfilestorage.util.MinioFrontMapper;
import com.tkachev.cloudfilestorage.util.MinioMapper;
import com.tkachev.cloudfilestorage.util.MinioUtil;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${spring.minio.bucket.name}")
    private String bucket; // или берем из @Value

    /**
     * Универсальный метод для работы с ресурсами пользователя
     *
     * @param userId - id пользователя
     * @param path   - полный путь к ресурсу в url-encoded формате
     * @return FrontResourceDTO или список ресурсов
     */
    @Override
    public FrontResourceDTO getResource(Integer userId, String path) throws Exception {
        boolean isDir = path.endsWith("/");
        path = MinioUtil.normalizePath(path, isDir);

        String userPrefix = MinioUtil.usePrefix(userId);

        String objectPath = path.startsWith(userPrefix) ? path : userPrefix + path;

        if (!objectPath.endsWith("/") && objectPath.contains(".")) {
            objectPath = objectPath.trim();
        }

        System.out.println("BUCKET: " + bucket);
        System.out.println("USER PREFIX: " + userPrefix);
        System.out.println("OBJECT PATH: " + objectPath);

        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectPath)
                            .build()
            );

            MinioObjectDTO dto = MinioObjectDTO.builder()
                    .name(MinioUtil.getName(objectPath))
                    .path(MinioUtil.getParentPath(objectPath))
                    .isDir(false)
                    .size((int) stat.size())
                    .build();

            return MinioFrontMapper.toFront(dto);

        } catch (io.minio.errors.ErrorResponseException e) {
            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(objectPath.endsWith("/") ? objectPath : objectPath + "/")
                            .recursive(false)
                            .build()
            );

            Iterator<Result<Item>> iterator = items.iterator();
            if (!iterator.hasNext()) {
                throw new FileNotFoundException(path);
            }

            MinioObjectDTO dto = MinioObjectDTO.builder()
                    .name(MinioUtil.getName(objectPath) + "/")
                    .path(MinioUtil.getParentPath(objectPath))
                    .isDir(true)
                    .size(0)
                    .build();

            return MinioFrontMapper.toFront(dto);
        }
    }


    /**
     * Создание папки
     */
    @Override
    public List<FrontResourceDTO> createDirectory(Integer userId, String path) {

        try (ByteArrayInputStream emptyContent = new ByteArrayInputStream(new byte[0])) {

            String userPrefix = MinioUtil.usePrefix(userId);
            boolean isDir = path.endsWith("/");
            String normalizedPath = MinioUtil.normalizePath(userPrefix + path, true);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(normalizedPath)
                            .stream(emptyContent, 0, -1)
                            .build()
            );

            String folderName = MinioUtil.getName(normalizedPath); // "folder32/"
            String parentPath = MinioUtil.getParentPath(normalizedPath).substring(userPrefix.length()); // "" если это корень

            MinioObjectDTO dto = MinioObjectDTO.builder()
                    .name(folderName)
                    .path(parentPath)
                    .isDir(true)
                    .build();


            return List.of(MinioFrontMapper.toFront(dto));
        } catch (Exception e) {
            throw new MinioDirectoryCreateException("Failed to create directory: " + path, e);
        }
    }





    /**
     * Загрузка файлов в папку пользователя
     */
    @Override
    public List<FrontResourceDTO> uploadFiles(Integer userId, String userFolder, MultipartFile[] files) {
        String userPrefix = MinioUtil.usePrefix(userId);
        // всегда заканчиваем слэшем
        userFolder = MinioUtil.normalizePath(userFolder, true);
        String folderPath = userPrefix + userFolder;

        List<FrontResourceDTO> uploadedFiles = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                if (fileName == null || fileName.isEmpty()) continue;

                // убедимся, что объект формируется правильно
                String objectName = folderPath + fileName;

                try (InputStream in = file.getInputStream()) {
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucket)
                                    .object(objectName)
                                    .stream(in, file.getSize(), -1)
                                    .contentType(file.getContentType())
                                    .build()
                    );
                }

                MinioObjectDTO dto = MinioObjectDTO.builder()
                        .name(fileName)
                        .path(folderPath) // путь до родителя
                        .isDir(false)
                        .size((int) file.getSize())
                        .build();

                uploadedFiles.add(MinioFrontMapper.toFront(dto));
            }

            return uploadedFiles;

        } catch (Exception e) {
            log.error("Failed to upload files to folder {}", userFolder, e);
            throw new MinioFileUploadException("Failed to upload files", e);
        }
    }



    /**
     * Переименование файла
     */
    @Override
    public FrontResourceDTO renameFile(Integer userId, String oldPath, String newFileName) {
        System.out.println("Renaming file from " + oldPath + " to " + newFileName);
        String userPrefix = MinioUtil.usePrefix(userId);

        String relativeOldPath = oldPath.startsWith(userPrefix) ? oldPath.substring(userPrefix.length()) : oldPath;
        relativeOldPath = relativeOldPath.endsWith("/") ? relativeOldPath.substring(0, relativeOldPath.length() - 1) : relativeOldPath;


        String parentPath = MinioUtil.getParentPath(relativeOldPath);
        System.out.println("Parent path: " + parentPath);

        String cleanFileName = Paths.get(newFileName).getFileName().toString();

        String newObjectName = userPrefix + parentPath + cleanFileName;
        System.out.println("New object name: " + newObjectName);

        String fullOldPath = userPrefix + relativeOldPath;
        System.out.println("Full old path: " + fullOldPath);

        try {
            copyAndDelete(fullOldPath, newObjectName);
            int size = (int) getObjectSize(newObjectName);

            MinioObjectDTO dto = MinioObjectDTO.builder()
                    .name(cleanFileName)
                    .path(parentPath)
                    .isDir(false)
                    .size(size)
                    .build();

            return MinioFrontMapper.toFront(dto);

        } catch (Exception e) {
            log.error("Failed to rename file from {} to {}", oldPath, newFileName, e);
            throw new MinioFileRenameException("Failed to rename file: " + oldPath, e);
        }
    }

    private long getObjectSize(String objectName) throws Exception {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        ).size();
    }



    /**
     * Переименование папки
     */
    @Override
    public FrontResourceDTO renameDirectory(Integer userId, String oldPath, String newDirName) {
        String userPrefix = MinioUtil.usePrefix(userId);
        boolean isDir = oldPath.endsWith("/");
        String fullOldPath = MinioUtil.normalizePath(userPrefix + oldPath, isDir);
        String newFullPath = userPrefix + newDirName + "/";

        List<String> copiedObjects = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(fullOldPath)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                String oldObjectName = item.objectName();
                String newObjectName = oldObjectName.replaceFirst(fullOldPath, newFullPath);

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(bucket)
                                .object(newObjectName)
                                .source(
                                        CopySource.builder()
                                                .bucket(bucket)
                                                .object(oldObjectName)
                                                .build()
                                )
                                .build()
                );

                copiedObjects.add(newObjectName);
            }

            // Удаляем старые объекты
            for (Result<Item> result : minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(fullOldPath)
                            .recursive(true)
                            .build()
            )) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucket)
                                .object(result.get().objectName())
                                .build()
                );
            }

            // Возвращаем DTO с новой папкой
            MinioObjectDTO dto = MinioObjectDTO.builder()
                    .name(newDirName + "/")
                    .path(userPrefix)
                    .isDir(true)
                    .size(0)
                    .build();

            return MinioFrontMapper.toFront(dto);

        } catch (Exception e) {
            log.error("Failed to rename directory from {} to {}. Rolling back...", oldPath, newDirName, e);

            for (String copied : copiedObjects) {
                try {
                    minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(bucket)
                                    .object(copied)
                                    .build()
                    );
                } catch (Exception rollbackEx) {
                    log.error("Failed to rollback object {}", copied, rollbackEx);
                }
            }

            throw new MinioDirectoryRenameException("Failed to rename directory and rolled back changes", e);
        }
    }



    private void copyAndDelete(String sourceObject, String targetObject) throws Exception {
        // Копируем
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(bucket)
                        .object(targetObject)
                        .source(
                                CopySource.builder()
                                        .bucket(bucket)
                                        .object(sourceObject)
                                        .build()
                        )
                        .build()
        );

        // Удаляем старый объект
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(sourceObject)
                        .build()
        );

        log.info("Moved object from {} to {}", sourceObject, targetObject);
    }




    /**
     * Получение списка файлов/папок в директории пользователя
     */
    @Override
    public List<FrontResourceDTO> folderList(Integer userId, String path) {
        String userPrefix = MinioUtil.usePrefix(userId);
        boolean isDir = path.endsWith("/");
        String fullPath = MinioUtil.normalizePath(userPrefix + path, isDir);

        if (!fullPath.endsWith("/")) {
            fullPath += "/";
        }

        System.out.println("BUCKET: " + bucket);
        System.out.println("FULL PATH: " + fullPath);

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(fullPath)
                            .recursive(false)
                            .build()
            );

            List<FrontResourceDTO> responseList = new ArrayList<>();
            for (Result<Item> result : results) {
                Item item = result.get();

                // Пропускаем саму текущую папку
                if (item.objectName().equals(fullPath)) continue;

                MinioObjectDTO dto = MinioMapper.convert(item);

                // Формируем путь относительно user-{id}-files/ для фронта
                String relativePath = fullPath.substring(userPrefix.length());
                dto.setPath(relativePath);

                responseList.add(MinioFrontMapper.toFront(dto));
            }

            return responseList;
        } catch (Exception e) {
            throw new MinioFolderReadException("Failed to list folder: " + path, e);
        }
    }



    /**
     * Удаление файла/папки
     */
    @Override
    public void deleteResource(Integer userId, String path) {
        String userPrefix = MinioUtil.usePrefix(userId);
        boolean isDir = path.endsWith("/");
        // Полный путь в бакете
        String fullPath = MinioUtil.normalizePath(userPrefix + path, isDir);

        try {
            // Получаем все объекты внутри папки (или сам файл)
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(fullPath)
                            .recursive(true)
                            .build()
            );

            List<DeleteObject> objectsToDelete = new ArrayList<>();
            for (Result<Item> result : results) {
                Item item = result.get();
                System.out.println("Deleting object: " + item.objectName());
                objectsToDelete.add(new DeleteObject(item.objectName()));
            }

            // Если это пустая папка, добавляем объект с "/"
            if (objectsToDelete.isEmpty() && isDir) {
                System.out.println("Deleting empty folder object: " + fullPath);
                objectsToDelete.add(new DeleteObject(fullPath));
            }

            if (!objectsToDelete.isEmpty()) {
                Iterable<Result<DeleteError>> errors = minioClient.removeObjects(
                        RemoveObjectsArgs.builder()
                                .bucket(bucket)
                                .objects(objectsToDelete)
                                .build()
                );

                for (Result<DeleteError> error : errors) {
                    DeleteError err = error.get();
                    System.err.println("Failed to delete object: " + err.objectName() + " - " + err.message());
                }
            }
        } catch (Exception e) {
            throw new MinioDeleteException("Failed to delete resource: " + path, e);
        }
    }



    @Override
    public List<FrontResourceDTO> searchResources(Integer userId, String query) {
        String userPrefix = MinioUtil.usePrefix(userId);

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(userPrefix)
                            .recursive(true)
                            .build()
            );

            List<FrontResourceDTO> found = new ArrayList<>();
            for (Result<Item> result : results) {
                Item item = result.get();
                String name = MinioUtil.getName(item.objectName());

                if (name != null && name.toLowerCase().contains(query.toLowerCase())) {
                    MinioObjectDTO dto = MinioMapper.convert(item);
                    found.add(MinioFrontMapper.toFront(dto));
                }
            }
            return found;
        } catch (Exception e) {
            throw new MinioSearchException("Failed to search resources by query: " + query, e);
        }
    }



}

