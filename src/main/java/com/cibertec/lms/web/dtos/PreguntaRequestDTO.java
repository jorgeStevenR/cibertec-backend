package com.cibertec.lms.web.dtos;

import java.util.List;

import com.cibertec.lms.web.validacion.UnaOpcionCorrecta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record PreguntaRequestDTO(

        @NotBlank(message = "El enunciado es obligatorio")
        String enunciado,

        @Positive(message = "El puntaje debe ser mayor a cero")
        int puntaje,

        @NotEmpty(message = "La pregunta debe tener al menos una opción")
        @Valid
        @UnaOpcionCorrecta
        List<OpcionRequestDTO> opciones
) {
}
