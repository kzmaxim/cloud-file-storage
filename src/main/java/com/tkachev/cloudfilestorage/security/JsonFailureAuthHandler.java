package com.tkachev.cloudfilestorage.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkachev.cloudfilestorage.dto.ErrorDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class JsonFailureAuthHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    public JsonFailureAuthHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorMessage = exception.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Authentication failed";
        }

        response.getWriter().write(objectMapper.writeValueAsString(new ErrorDTO(errorMessage)));
    }
}