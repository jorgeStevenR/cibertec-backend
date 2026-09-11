package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CursoActualizarRequestDTO(

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String descripcion,

        boolean publicado,

        @NotNull(message = "El id del programa es obligatorio")
        Long programaId
) {
}
