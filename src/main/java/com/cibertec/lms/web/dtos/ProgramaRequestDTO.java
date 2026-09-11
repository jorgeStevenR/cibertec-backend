package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProgramaRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        @Positive(message = "La duración en semestres debe ser mayor a cero")
        int duracionSemestres
) {
}
