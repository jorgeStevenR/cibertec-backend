package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** El tope (el puntaje de la pregunta) depende de la pregunta, así que se valida en el service. */
public record CalificarRespuestaRequestDTO(
        @NotNull(message = "El puntaje es obligatorio")
        @PositiveOrZero(message = "El puntaje no puede ser negativo")
        Double puntaje
) {
}
