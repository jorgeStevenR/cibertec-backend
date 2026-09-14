package com.cibertec.lms.dominio.cuestionario;

/** Par pregunta/opción elegida dentro de un intento; no tiene identidad propia fuera del intento que lo contiene. */
public record RespuestaValor(Long preguntaId, Long opcionId) {
}
