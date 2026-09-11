package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotNull;

public record MatriculaProgramaRequestDTO(

        @NotNull(message = "El id del programa es obligatorio")
        Long programaId,

        @NotNull(message = "El id del estudiante es obligatorio")
        Long estudianteId
) {
}
