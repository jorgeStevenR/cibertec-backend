package com.cibertec.lms.web.validacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.cibertec.lms.dominio.cuestionario.TipoPregunta;
import com.cibertec.lms.web.dtos.OpcionRequestDTO;
import com.cibertec.lms.web.dtos.PreguntaRequestDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class OpcionesSegunTipoValidatorTest {

    private static final OpcionRequestDTO CORRECTA = new OpcionRequestDTO(null, "Verdadero", true);
    private static final OpcionRequestDTO INCORRECTA = new OpcionRequestDTO(null, "Falso", false);

    private static ValidatorFactory fabrica;
    private static Validator validador;

    @BeforeAll
    static void crearValidador() {
        fabrica = Validation.buildDefaultValidatorFactory();
        validador = fabrica.getValidator();
    }

    @AfterAll
    static void cerrar() {
        fabrica.close();
    }

    @Test
    void sinTipoSeValidaComoOpcionMultiple() {
        assertTrue(errores(null, List.of(CORRECTA, INCORRECTA, INCORRECTA)).isEmpty());
        assertEquals("Debe haber exactamente una opción marcada como correcta",
                mensajeDeOpciones(null, List.of(INCORRECTA, INCORRECTA)));
    }

    @Test
    void verdaderoFalsoLlevaExactamenteDosOpcionesYUnaCorrecta() {
        assertTrue(errores(TipoPregunta.VERDADERO_FALSO, List.of(CORRECTA, INCORRECTA)).isEmpty());
        assertEquals("Una pregunta de verdadero/falso lleva exactamente dos opciones y una sola correcta",
                mensajeDeOpciones(TipoPregunta.VERDADERO_FALSO, List.of(CORRECTA, INCORRECTA, INCORRECTA)));
    }

    @Test
    void abiertaNoLlevaOpciones() {
        assertTrue(errores(TipoPregunta.ABIERTA, null).isEmpty());
        assertTrue(errores(TipoPregunta.ABIERTA, List.of()).isEmpty());
        assertEquals("Una pregunta abierta no lleva opciones", mensajeDeOpciones(TipoPregunta.ABIERTA, List.of(CORRECTA)));
    }

    private Set<ConstraintViolation<PreguntaRequestDTO>> errores(TipoPregunta tipo, List<OpcionRequestDTO> opciones) {
        return validador.validate(new PreguntaRequestDTO(tipo, "¿Enunciado?", 1, opciones));
    }

    /** Además del mensaje, comprueba que el error quede en el campo "opciones" (así llega en errores.opciones del 400). */
    private String mensajeDeOpciones(TipoPregunta tipo, List<OpcionRequestDTO> opciones) {
        Set<ConstraintViolation<PreguntaRequestDTO>> violaciones = errores(tipo, opciones);
        assertEquals(1, violaciones.size());
        ConstraintViolation<PreguntaRequestDTO> violacion = violaciones.iterator().next();
        assertEquals("opciones", violacion.getPropertyPath().toString());
        return violacion.getMessage();
    }
}
