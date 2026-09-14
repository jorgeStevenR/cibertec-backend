package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotBlank;

public record OpcionRequestDTO(

        /** Null = opción nueva. Si trae un id que no pertenece a la pregunta, se trata como nueva igual. */
        Long id,

        @NotBlank(message = "El texto de la opción es obligatorio")
        String texto,

        boolean esCorrecta
) {
}
