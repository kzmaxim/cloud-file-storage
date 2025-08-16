package com.tkachev.cloudfilestorage.dto;


import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MinioObjectDTO {
    private String path;
    private String name;
    private boolean isDir;
    private Integer size;

    public boolean getIsDir() {
        return isDir;
    }
}
