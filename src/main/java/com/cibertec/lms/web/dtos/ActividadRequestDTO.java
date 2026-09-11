package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ActividadRequestDTO(
        @NotBlank String titulo,
        String descripcion,
        LocalDateTime fechaEntrega,
        @NotNull @Positive Double puntajeMaximo
) {
}
