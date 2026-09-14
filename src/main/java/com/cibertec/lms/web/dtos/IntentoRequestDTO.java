package com.cibertec.lms.web.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record IntentoRequestDTO(

        @NotEmpty(message = "Debes responder al menos una pregunta")
        @Valid
        List<RespuestaRequestDTO> respuestas
) {
}
