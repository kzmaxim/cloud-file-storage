package com.tkachev.cloudfilestorage.controllers;

import com.tkachev.cloudfilestorage.dto.FrontResourceDTO;
import com.tkachev.cloudfilestorage.security.PersonDetails;
import com.tkachev.cloudfilestorage.services.minio.MinioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final MinioServiceImpl minioService;


    @GetMapping
    public ResponseEntity<List<FrontResourceDTO>> getDirectoryContents(
            @RequestParam(name="path") String path,
            @AuthenticationPrincipal PersonDetails personDetails) throws Exception {
        Integer userId = personDetails.getUserId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        if (decodedPath != null && !decodedPath.isBlank()) {
            List<FrontResourceDTO> resources = minioService.folderList(userId, decodedPath);
            return ResponseEntity.ok(resources);
        }

        List<FrontResourceDTO> resource = minioService.folderList(userId, "/");
        return ResponseEntity.ok(resource);
    }

    @PostMapping
    public ResponseEntity<List<FrontResourceDTO>> createDirectory(
            @RequestParam(name="path") String path,
            @AuthenticationPrincipal PersonDetails personDetails) {
        Integer userId = personDetails.getUserId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        List<FrontResourceDTO> resources = minioService.createDirectory(userId, decodedPath);
        return ResponseEntity.status(HttpStatus.CREATED).body(resources);
    }


}
