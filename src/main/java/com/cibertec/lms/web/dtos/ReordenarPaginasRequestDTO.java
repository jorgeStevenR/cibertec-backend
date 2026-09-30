package com.cibertec.lms.web.dtos;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record ReordenarPaginasRequestDTO(@NotEmpty List<Long> ids) {
}
