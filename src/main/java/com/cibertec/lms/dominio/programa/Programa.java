package com.cibertec.lms.dominio.programa;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Programa {

    private Long id;
    private String nombre;
    private String descripcion;
    private int duracionSemestres;
    private LocalDateTime fechaCreacion;
}
