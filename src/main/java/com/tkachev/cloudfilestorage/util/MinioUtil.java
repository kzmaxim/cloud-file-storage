package com.tkachev.cloudfilestorage.util;


import io.minio.messages.Item;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MinioUtil {
    public String usePrefix(Integer userId) {
        return "user-" + userId + "-files/";
    }
    /**
     * Получить имя файла или папки по полному пути.
     * Для папки убираем завершающий '/'.
     */
    public String getName(String objectName) {
        if (objectName.endsWith("/")) {
            objectName = objectName.substring(0, objectName.length() - 1);
        }
        int lastSlash = objectName.lastIndexOf('/');
        return (lastSlash >= 0) ? objectName.substring(lastSlash + 1) : objectName;
    }

    /**
     * Получить путь до родительской папки
     */
    public String getParentPath(String objectName) {
        if (objectName.endsWith("/")) {
            objectName = objectName.substring(0, objectName.length() - 1);
        }
        int lastSlash = objectName.lastIndexOf('/');
        return (lastSlash >= 0) ? objectName.substring(0, lastSlash + 1) : "";
    }

    /**
     * Создать UserDirectoryTree из объекта MinIO
     */
    public UserDirectoryTree toDirectoryTree(Item item) {
        String objectName = item.objectName();
        String directoryName = getName(objectName);
        String fullPath = objectName.endsWith("/") ? objectName : getParentPath(objectName);
        return new UserDirectoryTree(directoryName, fullPath);
    }

    /**
     * Преобразовать список объектов MinIO в список UserDirectoryTree
     */
    public List<UserDirectoryTree> toDirectoryTreeList(Iterable<Item> items) {
        List<UserDirectoryTree> treeList = new ArrayList<>();
        for (Item item : items) {
            if (item.isDir()) {
                treeList.add(toDirectoryTree(item));
            }
        }
        return treeList;
    }

    /**
     * Нормализация пути: убираем двойные слэши, добавляем завершающий '/' для папок
     */
    public String normalizePath(String path, boolean isDir) {
        if (path == null || path.isEmpty()) return "";
        path = path.replaceAll("//+", "/");
        if (isDir) {
            return path.endsWith("/") ? path : path + "/";
        } else {
            return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        }
    }


    /**
     * Создать объект для загрузки файла в MinIO
     * Например, для MultipartFile
     */
    public String buildObjectName(String userFolder, String fileName) {
        boolean isDir = fileName.endsWith("/");
        userFolder = normalizePath(userFolder, isDir);
        return userFolder + fileName;
    }

    /**
     * Создать новый путь для переименования файла
     */
    public String createNewFilePath(String oldPath, String newFileName) {
        String parent = getParentPath(oldPath);
        return parent + newFileName;
    }

    /**
     * Создать новый путь для переименования директории
     */
    public String createNewDirectoryPath(String oldDirPath, String newDirName, String objectName) {
        String relative = objectName.substring(oldDirPath.length());
        boolean isDir = (getParentPath(oldDirPath) + newDirName).endsWith("/");
        return normalizePath((getParentPath(oldDirPath) + newDirName), isDir) + relative;
    }
}

