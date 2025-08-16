package com.tkachev.cloudfilestorage.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrontResourceDTO {
    @Schema(description = "Имя ресурса", example = "John")
    private String name;

    @Schema(description = "Размер ресурса в байтах", example = "1024")
    private Integer size;

    @Schema(description = "Путь к ресурсу", example = "/documents/report.pdf")
    private String path;

    @Schema(description = "Флаг, указывающий, является ли ресурс папкой", example = "false")
    private boolean folder;
}
