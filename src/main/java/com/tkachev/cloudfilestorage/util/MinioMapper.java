package com.tkachev.cloudfilestorage.util;


import com.tkachev.cloudfilestorage.dto.MinioObjectDTO;
import io.minio.Result;
import io.minio.messages.Item;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MinioMapper {

    public static MinioObjectDTO convert(Item item) {
        String path = item.objectName();
        if (path == null || path.isEmpty()) return null;

        boolean isDir = item.isDir() || path.endsWith("/");

        return MinioObjectDTO.builder()
                .name(MinioUtil.getName(path))
                .path(MinioUtil.getParentPath(path)) // только родительский путь
                .isDir(isDir)
                .size(isDir ? 0 : (int) item.size())
                .build();
    }




    public static List<MinioObjectDTO> convert(Iterable<Result<Item>> iterable) {

        try {
            return StreamSupport.stream(iterable.spliterator(), false)
                    .map(result -> {
                        try {
                            String path = result.get().objectName();
                            if (path != null && !path.isEmpty()) {
                                String name = MinioUtil.getName(path);

                                return MinioObjectDTO
                                        .builder()
                                        .name(name)
                                        .path(path)
                                        .isDir(result.get().isDir())
                                        .build();
                            }
                            return null;
                        } catch (Exception e) {
                            throw new RuntimeException("Error converting Minio objects", e);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (RuntimeException e) {
            throw new RuntimeException("Error converting Minio objects", e);
        }

    }
}
