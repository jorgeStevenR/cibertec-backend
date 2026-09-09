package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotNull;

public record MatriculaRequestDTO(

        @NotNull(message = "El id del curso es obligatorio")
        Long cursoId,

        @NotNull(message = "El id del estudiante es obligatorio")
        Long estudianteId
) {
}
