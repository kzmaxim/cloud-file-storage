package com.tkachev.cloudfilestorage.controllers;

import com.tkachev.cloudfilestorage.dto.SuccessResponseDTO;
import com.tkachev.cloudfilestorage.security.PersonDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<SuccessResponseDTO> getCurrentUser(
            @AuthenticationPrincipal PersonDetails personDetails
    ) {
        String username = personDetails.getUsername();

        return ResponseEntity.ok(new SuccessResponseDTO(username));
    }
}
