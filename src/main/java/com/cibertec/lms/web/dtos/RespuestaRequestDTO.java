package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotNull;

public record RespuestaRequestDTO(

        @NotNull(message = "El id de la pregunta es obligatorio")
        Long preguntaId,

        @NotNull(message = "El id de la opción es obligatorio")
        Long opcionId
) {
}
