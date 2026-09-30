package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.curso.PaginaModulo;

public record PaginaModuloResponseDTO(
        Long id,
        Long moduloId,
        String titulo,
        int orden,
        LocalDateTime fechaCreacion
) {
    public static PaginaModuloResponseDTO desde(PaginaModulo pagina) {
        return new PaginaModuloResponseDTO(
                pagina.getId(),
                pagina.getModuloId(),
                pagina.getTitulo(),
                pagina.getOrden(),
                pagina.getFechaCreacion()
        );
    }
}
