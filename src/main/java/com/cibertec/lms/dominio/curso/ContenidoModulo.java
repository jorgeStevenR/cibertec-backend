package com.cibertec.lms.dominio.curso;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ContenidoModulo {

    private Long id;
    private Long moduloId;
    private TipoContenido tipo;
    private String nombre;

    /** Ruta dentro del bucket; null cuando el tipo es ENLACE. */
    private String rutaArchivo;

    /** URL externa; solo se usa cuando el tipo es ENLACE. */
    private String url;

    private LocalDateTime fechaSubida;
}
