package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotNull;

public record CalificarRequestDTO(
        @NotNull Double nota,
        String comentario
) {
}
