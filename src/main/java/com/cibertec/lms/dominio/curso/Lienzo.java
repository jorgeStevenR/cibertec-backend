package com.cibertec.lms.dominio.curso;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Lienzo {

    private Long id;
    private Long moduloId;
    private String nombre;
    private int orden;
    private int ancho;
    private int alto;

    /** Arreglo JSON de elementos del lienzo (texto/imagen/video/personaje); estructura libre que el backend no valida campo a campo. */
    private String elementosJson;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
