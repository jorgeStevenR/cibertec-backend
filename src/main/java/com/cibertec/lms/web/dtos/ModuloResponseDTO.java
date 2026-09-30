package com.cibertec.lms.web.dtos;

import com.cibertec.lms.dominio.curso.Modulo;

public record ModuloResponseDTO(
        Long id,
        Long cursoId,
        String titulo,
        int orden,
        Long moduloPadreId
) {
    public static ModuloResponseDTO desde(Modulo modulo) {
        return new ModuloResponseDTO(
                modulo.getId(), modulo.getCursoId(), modulo.getTitulo(), modulo.getOrden(), modulo.getModuloPadreId());
    }
}
