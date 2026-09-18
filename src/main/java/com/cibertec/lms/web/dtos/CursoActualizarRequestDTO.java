package com.cibertec.lms.web.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Reemplazo completo. Los campos administrativos (codigo, periodo, cupoMaximo, fechaInicio, fechaFin) solo validan formato
 *  acá: que sean obligatorios lo exige el servicio únicamente para el ADMINISTRADOR (un PROFESOR puede omitirlos, pero si envía
 *  uno distinto del actual recibe 403). */
public record CursoActualizarRequestDTO(

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String descripcion,

        boolean publicado,

        @NotNull(message = "El id del programa es obligatorio")
        Long programaId,

        @Size(max = 30, message = "El código no puede superar los 30 caracteres")
        String codigo,

        @Pattern(regexp = "^\\d{4}-\\d{1,2}$", message = "El período debe tener el formato AAAA-N (ej. 2026-2)")
        String periodo,

        /** Null = sin límite de estudiantes. */
        @Min(value = 1, message = "El cupo máximo debe ser al menos 1")
        Integer cupoMaximo,

        LocalDate fechaInicio,

        LocalDate fechaFin
) {
}
