package com.cibertec.lms.web.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cibertec.lms.dominio.cuestionario.EstadoIntento;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;

class IntentoResponseDTOTest {

    private static final List<PreguntaConRespuestaResponseDTO> PREGUNTAS = List.of(
            new PreguntaConRespuestaResponseDTO(1L, null, "¿2+2?", 2, 0, List.of(), 10L, null, null));

    @Test
    void conLaNotaVisibleElEstudianteVeTodo() {
        IntentoResponseDTO dto = IntentoResponseDTO.paraEstudiante(intento(EstadoIntento.CALIFICADO), null, PREGUNTAS, true);

        assertTrue(dto.notaVisible());
        assertEquals(2.5, dto.nota());
        assertEquals(4.2, dto.notaSobre5());
        assertEquals(PREGUNTAS, dto.preguntas());
    }

    @Test
    void conLaNotaOcultaElEstudianteSoloVeLaConfirmacion() {
        IntentoResponseDTO dto = IntentoResponseDTO.paraEstudiante(intento(EstadoIntento.CALIFICADO), null, PREGUNTAS, false);

        assertFalse(dto.notaVisible());
        assertNull(dto.nota());
        assertNull(dto.notaSobre5());
        assertEquals(List.of(), dto.preguntas());
        assertEquals(EstadoIntento.CALIFICADO, dto.estado());
    }

    @Test
    void pendienteDeRevisionNoMuestraNotaPeroSiLasRespuestas() {
        IntentoResponseDTO dto = IntentoResponseDTO.paraEstudiante(intento(EstadoIntento.PENDIENTE_REVISION), null, PREGUNTAS, true);

        assertFalse(dto.notaVisible());
        assertNull(dto.nota());
        assertEquals(PREGUNTAS, dto.preguntas());
    }

    @Test
    void elProfesorVeLaNotaAunqueEsteOcultaParaElEstudiante() {
        IntentoResponseDTO dto = IntentoResponseDTO.paraProfesor(intento(EstadoIntento.CALIFICADO), null, PREGUNTAS, false);

        assertFalse(dto.notaVisible());   // informa que el estudiante no la ve
        assertEquals(2.5, dto.nota());
        assertEquals(4.2, dto.notaSobre5());
        assertEquals(PREGUNTAS, dto.preguntas());
    }

    private static IntentoCuestionario intento(EstadoIntento estado) {
        return IntentoCuestionario.builder()
                .id(1L).cuestionarioId(5L).estudianteId(8L)
                .nota(2.5).puntajeMaximo(3).estado(estado).fechaEntrega(LocalDateTime.now())
                .respuestas(List.of())
                .build();
    }
}
