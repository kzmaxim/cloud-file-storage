package com.tkachev.cloudfilestorage.services.minio;

import com.tkachev.cloudfilestorage.dto.FrontResourceDTO;
import com.tkachev.cloudfilestorage.dto.MinioObjectDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

@Service
public interface MinioService {
    FrontResourceDTO getResource(Integer userId, String path) throws Exception;
    List<FrontResourceDTO> createDirectory(Integer userId, String path);
    List<FrontResourceDTO> uploadFiles(Integer userId, String userFolder, MultipartFile[] files);
    FrontResourceDTO renameFile(Integer userId, String oldPath, String newFileName);
    FrontResourceDTO renameDirectory(Integer userId, String oldPath, String newDirName);
    List<FrontResourceDTO> folderList(Integer userId, String path);
    void deleteResource(Integer userId, String path);
    List<FrontResourceDTO> searchResources(Integer userId, String query) throws FileNotFoundException;
    void downloadResource(Integer userId, String path, HttpServletResponse response);
}
