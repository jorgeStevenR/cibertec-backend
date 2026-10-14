package com.cibertec.lms.dominio.comun;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CalificacionTest {

    @Test
    void convierteLosPuntosALaEscalaDeCinco() {
        assertEquals(2.5, Calificacion.sobre5(2.0, 4.0));
        assertEquals(0.0, Calificacion.sobre5(0.0, 4.0));
        assertEquals(5.0, Calificacion.sobre5(4.0, 4.0));
        assertEquals(3.8, Calificacion.sobre5(15.0, 20.0));
    }

    @Test
    void redondeaAUnDecimalHaciaArribaEnElMedio() {
        assertEquals(1.7, Calificacion.sobre5(1.0, 3.0));   // 1,666… → 1,7
        assertEquals(4.2, Calificacion.sobre5(2.5, 3.0));   // 4,166… → 4,2
        assertEquals(0.3, Calificacion.sobre5(0.25, 5.0));  // 0,25 exacto → HALF_UP 0,3
    }

    @Test
    void sinNotaOSinMaximoNoHayEscala() {
        assertNull(Calificacion.sobre5(null, 4.0));
        assertNull(Calificacion.sobre5(2.0, 0.0));
        assertNull(Calificacion.sobre5(2.0, null));
    }

    @Test
    void quedaDentroDelRangoAunqueLosPuntosSeSalgan() {
        assertEquals(5.0, Calificacion.sobre5(6.0, 4.0));
        assertEquals(0.0, Calificacion.sobre5(-1.0, 4.0));
    }
}
