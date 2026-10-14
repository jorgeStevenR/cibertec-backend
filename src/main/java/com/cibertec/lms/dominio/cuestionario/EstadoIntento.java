package com.cibertec.lms.dominio.cuestionario;

public enum EstadoIntento {
    /** La nota es definitiva: no quedan respuestas abiertas sin calificar. */
    CALIFICADO,
    /** Tiene al menos una respuesta abierta que el profesor todavía no calificó; la nota es parcial. */
    PENDIENTE_REVISION
}
