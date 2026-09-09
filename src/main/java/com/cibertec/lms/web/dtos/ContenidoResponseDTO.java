package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.curso.ContenidoModulo;
import com.cibertec.lms.dominio.curso.TipoContenido;

public record ContenidoResponseDTO(
        Long id,
        Long moduloId,
        TipoContenido tipo,
        String nombre,
        boolean visible,
        LocalDateTime fechaSubida
) {
    public static ContenidoResponseDTO desde(ContenidoModulo contenido) {
        return new ContenidoResponseDTO(
                contenido.getId(),
                contenido.getModuloId(),
                contenido.getTipo(),
                contenido.getNombre(),
                contenido.isVisible(),
                contenido.getFechaSubida()
        );
    }
}
