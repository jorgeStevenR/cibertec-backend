package com.cibertec.lms.aplicacion.cuestionario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cibertec.lms.aplicacion.curso.CursoService;
import com.cibertec.lms.aplicacion.curso.ModuloService;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.cuestionario.Cuestionario;
import com.cibertec.lms.dominio.cuestionario.EstadoIntento;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionarioRepositorio;
import com.cibertec.lms.dominio.cuestionario.Opcion;
import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.dominio.cuestionario.PreguntaRepositorio;
import com.cibertec.lms.dominio.cuestionario.RespuestaValor;
import com.cibertec.lms.dominio.cuestionario.TipoPregunta;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;

class IntentoCuestionarioServiceTest {

    private static final Long CUESTIONARIO = 5L;
    private static final Long MODULO = 3L;
    private static final Long CURSO = 2L;
    private static final Long ESTUDIANTE = 8L;
    private static final Long PROFESOR = 1L;
    private static final Long INTENTO = 40L;

    // Opción múltiple (id 10, correcta = 101), verdadero/falso (id 11, correcta = 111) y dos abiertas (12 y 13).
    private static final Pregunta OPCION_MULTIPLE = cerrada(10L, TipoPregunta.OPCION_MULTIPLE, 2, 101L, 102L);
    private static final Pregunta VERDADERO_FALSO = cerrada(11L, TipoPregunta.VERDADERO_FALSO, 1, 111L, 112L);
    private static final Pregunta ABIERTA = abierta(12L, 2);
    private static final Pregunta OTRA_ABIERTA = abierta(13L, 1);

    private IntentoCuestionarioRepositorio intentos;
    private PreguntaRepositorio preguntas;
    private CuestionarioService cuestionarios;
    private IntentoCuestionarioService service;

    @BeforeEach
    void preparar() {
        intentos = mock(IntentoCuestionarioRepositorio.class);
        preguntas = mock(PreguntaRepositorio.class);
        cuestionarios = mock(CuestionarioService.class);
        ModuloService modulos = mock(ModuloService.class);
        CursoService cursos = mock(CursoService.class);
        MatriculaRepositorio matriculas = mock(MatriculaRepositorio.class);
        service = new IntentoCuestionarioService(intentos, preguntas, cuestionarios, modulos, cursos, matriculas);

        when(cuestionarios.buscarPorId(CUESTIONARIO)).thenReturn(
                Cuestionario.builder().id(CUESTIONARIO).moduloId(MODULO).build());
        when(modulos.buscarPorId(MODULO)).thenReturn(Modulo.builder().id(MODULO).cursoId(CURSO).build());
        when(cursos.buscarPorId(CURSO)).thenReturn(Curso.builder().id(CURSO).build());
        when(matriculas.existePorCursoYEstudiante(CURSO, ESTUDIANTE)).thenReturn(true);
        when(intentos.guardar(any(IntentoCuestionario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(preguntas.listarPorCuestionario(CUESTIONARIO))
                .thenReturn(List.of(OPCION_MULTIPLE, VERDADERO_FALSO, ABIERTA, OTRA_ABIERTA));
        for (Pregunta p : List.of(OPCION_MULTIPLE, VERDADERO_FALSO, ABIERTA, OTRA_ABIERTA)) {
            when(preguntas.buscarPorId(p.getId())).thenReturn(Optional.of(p));
        }
    }

    @Test
    void lasCerradasSeCorrigenSolasYUnaAbiertaConTextoDejaElIntentoPendiente() {
        IntentoCuestionario intento = service.responder(CUESTIONARIO, List.of(
                RespuestaValor.cerrada(10L, 101L),   // bien: +2
                RespuestaValor.cerrada(11L, 112L),   // mal: +0
                RespuestaValor.abierta(12L, "  La fotosíntesis convierte luz en energía  ")
        ), ESTUDIANTE);

        assertEquals(2.0, intento.getNota());
        assertEquals(6.0, intento.getPuntajeMaximo());
        assertEquals(EstadoIntento.PENDIENTE_REVISION, intento.getEstado());

        RespuestaValor abierta = intento.getRespuestas().stream().filter(r -> r.preguntaId().equals(12L)).findFirst().orElseThrow();
        assertEquals("La fotosíntesis convierte luz en energía", abierta.texto());
        assertNull(abierta.opcionId());
        assertNull(abierta.puntajeObtenido());
    }

    @Test
    void sinAbiertasRespondidasElIntentoQuedaCalificado() {
        IntentoCuestionario intento = service.responder(CUESTIONARIO, List.of(
                RespuestaValor.cerrada(10L, 101L),
                RespuestaValor.cerrada(11L, 111L),
                RespuestaValor.abierta(12L, "   ")   // en blanco: cuenta como no respondida
        ), ESTUDIANTE);

        assertEquals(3.0, intento.getNota());
        assertEquals(EstadoIntento.CALIFICADO, intento.getEstado());
        assertEquals(2, intento.getRespuestas().size());
    }

    @Test
    void calificarLaUltimaAbiertaPendienteSumaElPuntajeYCierraElIntento() {
        intentoGuardado(2.0, EstadoIntento.PENDIENTE_REVISION, RespuestaValor.abierta(12L, "texto"));

        service.calificarRespuesta(INTENTO, 12L, 1.5, PROFESOR, Rol.PROFESOR);

        verify(intentos).calificarRespuesta(INTENTO, 12L, 1.5, 3.5, EstadoIntento.CALIFICADO);
    }

    @Test
    void recalificarReemplazaElPuntajeAnteriorEnVezDeSumarlo() {
        intentoGuardado(3.5, EstadoIntento.CALIFICADO, new RespuestaValor(12L, null, "texto", 1.5));

        service.calificarRespuesta(INTENTO, 12L, 0.5, PROFESOR, Rol.PROFESOR);

        verify(intentos).calificarRespuesta(INTENTO, 12L, 0.5, 2.5, EstadoIntento.CALIFICADO);
    }

    @Test
    void siQuedaOtraAbiertaSinCalificarElIntentoSiguePendiente() {
        intentoGuardado(2.0, EstadoIntento.PENDIENTE_REVISION,
                RespuestaValor.abierta(12L, "texto"), RespuestaValor.abierta(13L, "otro texto"));

        service.calificarRespuesta(INTENTO, 12L, 2.0, PROFESOR, Rol.PROFESOR);

        verify(intentos).calificarRespuesta(INTENTO, 12L, 2.0, 4.0, EstadoIntento.PENDIENTE_REVISION);
    }

    @Test
    void noSePuedeDarMasQueElPuntajeDeLaPregunta() {
        intentoGuardado(2.0, EstadoIntento.PENDIENTE_REVISION, RespuestaValor.abierta(12L, "texto"));

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> service.calificarRespuesta(INTENTO, 12L, 2.5, PROFESOR, Rol.PROFESOR));

        assertEquals("El puntaje debe estar entre 0 y 2", error.getMessage());
        verify(intentos, never()).calificarRespuesta(anyLong(), anyLong(), anyDouble(), anyDouble(), any());
    }

    @Test
    void unaPreguntaCerradaNoSeCalificaAMano() {
        intentoGuardado(2.0, EstadoIntento.CALIFICADO, RespuestaValor.cerrada(10L, 101L));

        assertThrows(ReglaNegocioException.class,
                () -> service.calificarRespuesta(INTENTO, 10L, 1.0, PROFESOR, Rol.PROFESOR));
        verify(intentos, never()).calificarRespuesta(anyLong(), anyLong(), anyDouble(), anyDouble(), any());
    }

    @Test
    void calificarValidaQueSeaElProfesorDelCurso() {
        intentoGuardado(2.0, EstadoIntento.PENDIENTE_REVISION, RespuestaValor.abierta(12L, "texto"));

        service.calificarRespuesta(INTENTO, 12L, 1.0, PROFESOR, Rol.PROFESOR);

        verify(cuestionarios).validarPropietarioDelModulo(eq(MODULO), eq(PROFESOR), eq(Rol.PROFESOR));
    }

    private void intentoGuardado(double nota, EstadoIntento estado, RespuestaValor... respuestas) {
        when(intentos.buscarPorId(INTENTO)).thenReturn(Optional.of(IntentoCuestionario.builder()
                .id(INTENTO).cuestionarioId(CUESTIONARIO).estudianteId(ESTUDIANTE)
                .nota(nota).puntajeMaximo(6).estado(estado).fechaEntrega(LocalDateTime.now())
                .respuestas(List.of(respuestas))
                .build()));
    }

    private static Pregunta cerrada(Long id, TipoPregunta tipo, int puntaje, Long correctaId, Long incorrectaId) {
        return Pregunta.builder().id(id).cuestionarioId(CUESTIONARIO).tipo(tipo).puntaje(puntaje).opciones(List.of(
                Opcion.builder().id(correctaId).preguntaId(id).texto("Sí").esCorrecta(true).build(),
                Opcion.builder().id(incorrectaId).preguntaId(id).texto("No").esCorrecta(false).build()
        )).build();
    }

    private static Pregunta abierta(Long id, int puntaje) {
        return Pregunta.builder().id(id).cuestionarioId(CUESTIONARIO).tipo(TipoPregunta.ABIERTA).puntaje(puntaje)
                .opciones(List.of()).build();
    }
}
