package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CuestionarioRequestDTO(

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String descripcion,

        /** Null = disponible desde que se crea. */
        LocalDateTime fechaApertura,

        /** Null = sin fecha límite. */
        LocalDateTime fechaEntrega,

        /** Null = sin límite de tiempo. Informativo por ahora: no se corta el intento automáticamente. */
        @Positive(message = "El tiempo límite debe ser mayor a cero")
        Integer tiempoLimiteMinutos,

        /** Si el estudiante ve su nota y la revisión de sus respuestas. Null = true. */
        Boolean mostrarNota
) {

    public boolean mostrarNotaEfectiva() {
        return mostrarNota == null || mostrarNota;
    }
}
