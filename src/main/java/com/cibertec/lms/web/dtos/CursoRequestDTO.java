package com.cibertec.lms.web.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CursoRequestDTO(

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String descripcion,

        @NotNull(message = "El id del profesor es obligatorio")
        Long profesorId,

        @NotNull(message = "El id del programa es obligatorio")
        Long programaId,

        @NotBlank(message = "El código es obligatorio")
        @Size(max = 30, message = "El código no puede superar los 30 caracteres")
        String codigo,

        @NotBlank(message = "El período es obligatorio")
        @Pattern(regexp = "^\\d{4}-\\d{1,2}$", message = "El período debe tener el formato AAAA-N (ej. 2026-2)")
        String periodo,

        /** Null = sin límite de estudiantes. */
        @Min(value = 1, message = "El cupo máximo debe ser al menos 1")
        Integer cupoMaximo,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin
) {
}
