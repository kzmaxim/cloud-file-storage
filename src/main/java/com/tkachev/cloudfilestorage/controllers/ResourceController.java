package com.tkachev.cloudfilestorage.controllers;

import com.tkachev.cloudfilestorage.dto.FrontResourceDTO;
import com.tkachev.cloudfilestorage.security.PersonDetails;
import com.tkachev.cloudfilestorage.services.minio.MinioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final MinioServiceImpl minioService;


    @Operation(
            summary = "Получить ресурс по пути",
            description = "Возвращает информацию о ресурсе по указанному пути. " +
                    "Если ресурс не найден, возвращается ошибка 404."
    )
    @GetMapping
    public ResponseEntity<FrontResourceDTO> getResource(@RequestParam(name="path") String path,
                                                        @AuthenticationPrincipal PersonDetails personDetails) throws Exception {
        Integer userId = personDetails.getUserId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        FrontResourceDTO resource = minioService.getResource(userId, decodedPath);
        return ResponseEntity.ok(resource);
    }

    @Operation(
            summary = "Переместить ресурс",
            description = "Перемещает ресурс из одного пути в другой. " +
                    "Если перемещение успешно, возвращается информация о новом ресурсе."
    )
    @GetMapping("/move")
    public ResponseEntity<FrontResourceDTO> moveResource(@RequestParam(name="from") String oldPath,
                                             @RequestParam(name="to") String newFileName,
                                             @AuthenticationPrincipal PersonDetails personDetails) {
        Integer userId = personDetails.getUserId();
        String decodedFrom = URLDecoder.decode(oldPath, StandardCharsets.UTF_8);
        String decodedTo = URLDecoder.decode(newFileName, StandardCharsets.UTF_8);
        if (decodedFrom.endsWith("/") && decodedTo.endsWith("/")) {
            FrontResourceDTO response = minioService.renameDirectory(userId, decodedFrom, decodedTo);
            return ResponseEntity.ok(response);
        }
        FrontResourceDTO response = minioService.renameFile(userId, decodedFrom, decodedTo);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Поиск ресурсов",
            description = "Ищет ресурсы по заданному запросу. " +
                    "Возвращает список ресурсов, соответствующих запросу."
    )
    @GetMapping("/search")
    public ResponseEntity<List<FrontResourceDTO>> searchResources(
            @RequestParam(name="query") String query,
            @AuthenticationPrincipal PersonDetails personDetails) throws Exception {
        Integer userId = personDetails.getUserId();
        String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8);
        List<FrontResourceDTO> resources = minioService.searchResources(userId, decodedQuery);
        return ResponseEntity.ok(resources);
    }


    @Operation(
            summary = "Загрузить файлы",
            description = "Загружает один или несколько файлов в указанную директорию. " +
                    "Если параметр не указан, файлы загружаются в корневую директорию."
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FrontResourceDTO>> uploadFiles(
            @RequestPart("object") MultipartFile[] files,
            @RequestParam(name="path", required = false) String userFolder,
            @AuthenticationPrincipal PersonDetails personDetails) {
        Integer userId = personDetails.getUserId();
        String decodedUserFolder = URLDecoder.decode(userFolder, StandardCharsets.UTF_8);
        List<FrontResourceDTO> response = minioService.uploadFiles(userId, decodedUserFolder, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Удалить ресурс",
            description = "Удаляет ресурс по указанному пути. " +
                    "Если ресурс не найден, возвращается ошибка 404."
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteResource(
            @RequestParam(name="path") String path,
            @AuthenticationPrincipal PersonDetails personDetails) {
        Integer userId = personDetails.getUserId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        minioService.deleteResource(userId, decodedPath);
        return ResponseEntity.status(204).build();
    }


    @Operation(
            summary = "Скачать ресурс",
            description = "Скачивает ресурс по указанному пути. " +
                    "Если ресурс не найден, возвращается ошибка 404."
    )
    @GetMapping("/download")
    public ResponseEntity<FrontResourceDTO> downloadResource(
            @RequestParam(name="path") String path,
            @AuthenticationPrincipal PersonDetails personDetails,
            HttpServletResponse response) throws Exception {
        Integer userId = personDetails.getUserId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        minioService.downloadResource(userId, decodedPath, response);
        return ResponseEntity.ok().build();
    }
}
