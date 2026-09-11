package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tools.jackson.databind.JsonNode;

public record LienzoContenidoRequestDTO(
        @Positive int ancho,
        @Positive int alto,
        @NotNull JsonNode elementos) {
}
