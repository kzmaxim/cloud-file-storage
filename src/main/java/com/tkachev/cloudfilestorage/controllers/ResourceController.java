package com.tkachev.cloudfilestorage.controllers;

import com.tkachev.cloudfilestorage.dto.FrontResourceDTO;
import com.tkachev.cloudfilestorage.security.PersonDetails;
import com.tkachev.cloudfilestorage.services.minio.MinioServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<FrontResourceDTO> getResource(@RequestParam(name="path") String path,
                                                        @AuthenticationPrincipal PersonDetails personDetails) throws Exception {
        Integer userId = personDetails.getUserId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        FrontResourceDTO resource = minioService.getResource(userId, decodedPath);
        return ResponseEntity.ok(resource);
    }

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

    @GetMapping("/search")
    public ResponseEntity<List<FrontResourceDTO>> searchResources(
            @RequestParam(name="query") String query,
            @AuthenticationPrincipal PersonDetails personDetails) throws Exception {
        Integer userId = personDetails.getUserId();
        String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8);
        List<FrontResourceDTO> resources = minioService.searchResources(userId, decodedQuery);
        return ResponseEntity.ok(resources);
    }

    @PostMapping
    public ResponseEntity<List<FrontResourceDTO>> uploadFiles(
            @RequestParam("object") MultipartFile[] files,
            @RequestParam(name="path", required = false) String userFolder,
            @AuthenticationPrincipal PersonDetails personDetails) {
        Integer userId = personDetails.getUserId();
        String decodedUserFolder = URLDecoder.decode(userFolder, StandardCharsets.UTF_8);
        List<FrontResourceDTO> response = minioService.uploadFiles(userId, decodedUserFolder, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteResource(
            @RequestParam(name="path") String path,
            @AuthenticationPrincipal PersonDetails personDetails) {
        Integer userId = personDetails.getUserId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        minioService.deleteResource(userId, decodedPath);
        return ResponseEntity.status(204).build();
    }


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
