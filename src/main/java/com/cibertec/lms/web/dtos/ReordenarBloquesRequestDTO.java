package com.cibertec.lms.web.dtos;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record ReordenarBloquesRequestDTO(@NotEmpty List<Long> ids) {
}
