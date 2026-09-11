package com.cibertec.lms.web.dtos;

import jakarta.validation.constraints.NotBlank;

public record VideoBloqueRequestDTO(@NotBlank String urlVideo) {
}
