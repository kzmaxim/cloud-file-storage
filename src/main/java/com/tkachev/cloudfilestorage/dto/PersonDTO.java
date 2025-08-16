package com.tkachev.cloudfilestorage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    @NotNull(message="Username should not be null")
    private String username;

    @NotNull(message="Password should not be null")
    private String password;
}
