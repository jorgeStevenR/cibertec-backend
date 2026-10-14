package com.cibertec.lms.dominio.cuestionario;

/** Respuesta a una pregunta dentro de un intento; no tiene identidad propia fuera del intento que lo contiene.
 *  En preguntas cerradas solo se usa {@code opcionId}. En abiertas se usan {@code texto} y {@code puntajeObtenido},
 *  que queda en null hasta que el profesor la califica. */
public record RespuestaValor(Long preguntaId, Long opcionId, String texto, Double puntajeObtenido) {

    public static RespuestaValor cerrada(Long preguntaId, Long opcionId) {
        return new RespuestaValor(preguntaId, opcionId, null, null);
    }

    public static RespuestaValor abierta(Long preguntaId, String texto) {
        return new RespuestaValor(preguntaId, null, texto, null);
    }
}
