package com.cibertec.lms.web.dtos;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

/** moduloPadreId null = reordena los módulos raíz del curso; si no, reordena los hijos de ese módulo. */
public record ReordenarModulosRequestDTO(Long moduloPadreId, @NotEmpty List<Long> ids) {
}
