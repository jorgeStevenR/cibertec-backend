package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> errores
) {
    public static ErrorResponseDTO de(int status, String error, String message, String path) {
        return new ErrorResponseDTO(LocalDateTime.now(), status, error, message, path, null);
    }

    public static ErrorResponseDTO deValidacion(int status, String error, String message, String path, Map<String, String> errores) {
        return new ErrorResponseDTO(LocalDateTime.now(), status, error, message, path, errores);
    }
}
