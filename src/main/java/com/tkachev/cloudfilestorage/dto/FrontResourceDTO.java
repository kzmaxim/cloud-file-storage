package com.tkachev.cloudfilestorage.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrontResourceDTO {
    private String name;
    private Integer size;
    private String path;
    private boolean folder;


    public static FrontResourceDTO fromMinio(MinioObjectDTO obj) {
        String fullPath = obj.getPath();
        if (obj.getIsDir() && !fullPath.endsWith("/")) fullPath += "/";
        return new FrontResourceDTO(
                obj.getName(),
                obj.getIsDir() ? null : 0,
                fullPath,
                obj.getIsDir()
        );
    }
}
