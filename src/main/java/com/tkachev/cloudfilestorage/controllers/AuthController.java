package com.tkachev.cloudfilestorage.controllers;


import com.tkachev.cloudfilestorage.dto.PersonDTO;
import com.tkachev.cloudfilestorage.dto.SuccessResponseDTO;
import com.tkachev.cloudfilestorage.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Зарегистрировать нового пользователя",
            description = "Регистрация нового пользователя в системе. " +
                    "Требуется передать данные пользователя в формате JSON, " +
                    "{username: ...., password: ....}"
    )
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid PersonDTO personDTO) {
        authService.registerUser(personDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponseDTO(personDTO.getUsername()));
    }
}
