package com.cibertec.lms.dominio.cuestionario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Opcion {

    /** Null cuando todavía no existe en la base (opción nueva dentro de un reemplazo de pregunta). */
    private Long id;

    private Long preguntaId;
    private String texto;
    private boolean esCorrecta;
}
