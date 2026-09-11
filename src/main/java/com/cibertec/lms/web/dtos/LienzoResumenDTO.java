package com.cibertec.lms.web.dtos;

import com.cibertec.lms.dominio.curso.Lienzo;

public record LienzoResumenDTO(Long id, String nombre, int orden) {

    public static LienzoResumenDTO desde(Lienzo lienzo) {
        return new LienzoResumenDTO(lienzo.getId(), lienzo.getNombre(), lienzo.getOrden());
    }
}
