package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotBlank;

public record CursoActualizarRequestDTO(

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String descripcion,

        boolean publicado
) {
}
