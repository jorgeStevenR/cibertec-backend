package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.curso.BloqueContenido;
import com.cibertec.lms.dominio.curso.TipoBloque;

public record BloqueResponseDTO(
        Long id,
        Long moduloId,
        Long paginaId,
        TipoBloque tipo,
        String texto,
        String nombreArchivo,
        String urlVideo,
        int orden,
        LocalDateTime fechaCreacion
) {
    public static BloqueResponseDTO desde(BloqueContenido bloque) {
        return new BloqueResponseDTO(
                bloque.getId(),
                bloque.getModuloId(),
                bloque.getPaginaId(),
                bloque.getTipo(),
                bloque.getTexto(),
                bloque.getNombreArchivo(),
                bloque.getUrlVideo(),
                bloque.getOrden(),
                bloque.getFechaCreacion()
        );
    }
}
