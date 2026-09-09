package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotBlank;

public record ModuloRequestDTO(

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        int orden
) {
}
