package com.cibertec.lms.dominio.curso;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BloqueContenido {

    private Long id;
    private Long moduloId;

    /** Página a la que pertenece el bloque; null si es un bloque suelto del módulo. Si no es null, moduloId es el de esa página. */
    private Long paginaId;

    private TipoBloque tipo;

    /** Solo cuando el tipo es TEXTO. */
    private String texto;

    /** Ruta dentro del bucket; solo cuando el tipo es IMAGEN o ARCHIVO. */
    private String rutaArchivo;

    /** Nombre original del archivo; solo cuando el tipo es IMAGEN o ARCHIVO. */
    private String nombreArchivo;

    /** URL del video (YouTube u otro); solo cuando el tipo es VIDEO. */
    private String urlVideo;

    private int orden;
    private LocalDateTime fechaCreacion;
}
