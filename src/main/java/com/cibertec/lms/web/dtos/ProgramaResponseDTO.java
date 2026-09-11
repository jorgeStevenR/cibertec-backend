package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.programa.Programa;

public record ProgramaResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        int duracionSemestres,
        LocalDateTime fechaCreacion
) {
    public static ProgramaResponseDTO desde(Programa programa) {
        return new ProgramaResponseDTO(
                programa.getId(),
                programa.getNombre(),
                programa.getDescripcion(),
                programa.getDuracionSemestres(),
                programa.getFechaCreacion()
        );
    }
}
