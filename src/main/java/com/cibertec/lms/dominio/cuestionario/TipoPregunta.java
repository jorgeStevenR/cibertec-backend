package com.cibertec.lms.dominio.cuestionario;

public enum TipoPregunta {
    /** Varias opciones, exactamente una correcta. Se califica automáticamente. */
    OPCION_MULTIPLE,
    /** Exactamente dos opciones (Verdadero / Falso), una correcta. Se califica automáticamente. */
    VERDADERO_FALSO,
    /** Sin opciones: el estudiante escribe un texto y el profesor le asigna un puntaje a mano. */
    ABIERTA;

    public boolean esCerrada() {
        return this != ABIERTA;
    }
}
