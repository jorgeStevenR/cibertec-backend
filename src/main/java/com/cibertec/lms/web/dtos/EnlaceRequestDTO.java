package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotBlank;

public record EnlaceRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La url es obligatoria")
        String url
) {
}
