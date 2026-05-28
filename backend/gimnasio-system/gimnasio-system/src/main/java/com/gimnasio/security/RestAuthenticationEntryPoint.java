package com.gimnasio.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gimnasio.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        System.out.println("[RestAuthenticationEntryPoint] commence triggered. servletPath=" + request.getServletPath()
                + ", requestURI=" + request.getRequestURI()
                + ", authException=" + (authException != null ? authException.getClass().getName() : "null")
                + ", message=" + (authException != null ? authException.getMessage() : "null"));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "No autenticado o token invalido",
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), error);
    }

}
