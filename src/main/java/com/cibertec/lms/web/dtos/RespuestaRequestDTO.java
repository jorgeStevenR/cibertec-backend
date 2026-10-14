package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RespuestaRequestDTO(

        @NotNull(message = "El id de la pregunta es obligatorio")
        Long preguntaId,

        /** Para preguntas cerradas (opción múltiple y verdadero/falso). */
        Long opcionId,

        /** Para preguntas abiertas. */
        @Size(max = 5000, message = "La respuesta no puede superar los 5000 caracteres")
        String texto
) {
}
