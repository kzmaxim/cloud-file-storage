package com.tkachev.cloudfilestorage.util;

import com.tkachev.cloudfilestorage.dto.FrontResourceDTO;
import com.tkachev.cloudfilestorage.dto.MinioObjectDTO;

public class MinioFrontMapper {

    public static FrontResourceDTO toFront(MinioObjectDTO obj) {
        String parentPath = obj.getPath();
        if (parentPath.equals("/")) parentPath = "";

        String name = obj.getName();
        boolean isDir = obj.getIsDir();

        if (isDir && !name.endsWith("/")) {
            name += "/";
        }

        // Убираем MinIO префикс (user-{id}-files/)
        String relativePath = parentPath.replaceFirst("^user-\\d+-files/", "");

        return new FrontResourceDTO(
                name,
                isDir ? 0 : obj.getSize(),
                relativePath,
                isDir
        );
    }


}

