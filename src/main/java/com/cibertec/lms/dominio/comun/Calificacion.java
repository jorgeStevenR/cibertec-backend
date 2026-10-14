package com.cibertec.lms.dominio.comun;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Conversión de una nota en puntos a la escala colombiana de 0,0 a 5,0. Las notas se guardan siempre en puntos;
 *  la escala sobre 5 se calcula al responder, nunca se persiste. */
public final class Calificacion {

    private static final BigDecimal CINCO = BigDecimal.valueOf(5);

    private Calificacion() {
    }

    /** nota / maximo × 5, redondeado a 1 decimal (HALF_UP) y acotado a [0,0 ; 5,0].
     *  Null si no hay nota (pendiente, sin calificar u oculta) o si el máximo es null o 0 (no hay escala sobre la cual medir). */
    public static Double sobre5(Double nota, Double maximo) {
        if (nota == null || maximo == null || maximo <= 0) {
            return null;
        }
        BigDecimal resultado = BigDecimal.valueOf(nota)
                .multiply(CINCO)
                .divide(BigDecimal.valueOf(maximo), 1, RoundingMode.HALF_UP);
        return resultado.max(BigDecimal.ZERO).min(CINCO).doubleValue();
    }
}
