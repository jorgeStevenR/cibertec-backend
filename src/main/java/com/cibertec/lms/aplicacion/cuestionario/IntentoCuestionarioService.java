package com.cibertec.lms.aplicacion.cuestionario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.aplicacion.curso.CursoService;
import com.cibertec.lms.aplicacion.curso.ModuloService;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.cuestionario.Cuestionario;
import com.cibertec.lms.dominio.cuestionario.EstadoIntento;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionarioRepositorio;
import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.dominio.cuestionario.PreguntaRepositorio;
import com.cibertec.lms.dominio.cuestionario.RespuestaValor;
import com.cibertec.lms.dominio.cuestionario.TipoPregunta;
import com.cibertec.lms.dominio.excepcion.IntentoNoEncontradoException;
import com.cibertec.lms.dominio.excepcion.PreguntaNoEncontradaException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.excepcion.VentanaCuestionarioException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IntentoCuestionarioService {

    private final IntentoCuestionarioRepositorio intentoCuestionarioRepositorio;
    private final PreguntaRepositorio preguntaRepositorio;
    private final CuestionarioService cuestionarioService;
    private final ModuloService moduloService;
    private final CursoService cursoService;
    private final MatriculaRepositorio matriculaRepositorio;

    /** Corrección al enviar: en las preguntas cerradas se busca la opción elegida y, si es la marcada esCorrecta=true,
     *  suma su puntaje. Las abiertas con texto quedan sin puntaje hasta que el profesor las califique, y el intento queda
     *  PENDIENTE_REVISION. Una respuesta ausente o en blanco, o que apunte a una pregunta/opción que no pertenece a este
     *  cuestionario, sencillamente no suma - el efecto neto es el mismo que responder mal, así que no se rechaza. */
    public IntentoCuestionario responder(Long cuestionarioId, List<RespuestaValor> respuestasEnviadas, Long estudianteId) {
        Cuestionario cuestionario = cuestionarioService.buscarPorId(cuestionarioId);
        validarMatriculado(cuestionario, estudianteId);

        if (intentoCuestionarioRepositorio.existePorCuestionarioYEstudiante(cuestionarioId, estudianteId)) {
            throw new ReglaNegocioException("Ya respondiste este cuestionario");
        }
        if (cuestionarioService.aunNoAbre(cuestionario)) {
            throw new VentanaCuestionarioException("Este cuestionario todavía no está disponible.");
        }
        if (cuestionarioService.yaVencio(cuestionario)) {
            throw new VentanaCuestionarioException("El plazo para responder este cuestionario ya venció.");
        }

        List<Pregunta> preguntas = preguntaRepositorio.listarPorCuestionario(cuestionarioId);
        Map<Long, RespuestaValor> enviadaPorPregunta = respuestasEnviadas.stream()
                .collect(Collectors.toMap(RespuestaValor::preguntaId, r -> r, (a, b) -> b));

        double nota = 0;
        double puntajeMaximo = 0;
        boolean hayAbiertasPorRevisar = false;
        List<RespuestaValor> respuestasValidas = new ArrayList<>();

        for (Pregunta pregunta : preguntas) {
            puntajeMaximo += pregunta.getPuntaje();
            RespuestaValor enviada = enviadaPorPregunta.get(pregunta.getId());
            if (enviada == null) {
                continue;
            }

            if (pregunta.getTipo() == TipoPregunta.ABIERTA) {
                if (enviada.texto() == null || enviada.texto().isBlank()) {
                    continue;
                }
                respuestasValidas.add(RespuestaValor.abierta(pregunta.getId(), enviada.texto().strip()));
                hayAbiertasPorRevisar = true;
                continue;
            }

            Long opcionElegidaId = enviada.opcionId();
            if (opcionElegidaId == null) {
                continue;
            }
            respuestasValidas.add(RespuestaValor.cerrada(pregunta.getId(), opcionElegidaId));

            boolean esCorrecta = pregunta.getOpciones().stream()
                    .anyMatch(o -> o.getId().equals(opcionElegidaId) && o.isEsCorrecta());
            if (esCorrecta) {
                nota += pregunta.getPuntaje();
            }
        }

        IntentoCuestionario nuevo = IntentoCuestionario.builder()
                .cuestionarioId(cuestionarioId)
                .estudianteId(estudianteId)
                .nota(nota)
                .puntajeMaximo(puntajeMaximo)
                .estado(hayAbiertasPorRevisar ? EstadoIntento.PENDIENTE_REVISION : EstadoIntento.CALIFICADO)
                .fechaEntrega(LocalDateTime.now())
                .respuestas(respuestasValidas)
                .build();

        return intentoCuestionarioRepositorio.guardar(nuevo);
    }

    /** El profesor asigna (o corrige) el puntaje de una respuesta abierta, entre 0 y el puntaje actual de la pregunta.
     *  La nota del intento se ajusta por la diferencia con lo que tenía antes esa respuesta, así que las cerradas no se
     *  recalculan. Cuando no quedan abiertas sin puntaje, el intento pasa a CALIFICADO. */
    public IntentoCuestionario calificarRespuesta(
            Long intentoId, Long preguntaId, double puntaje, Long usuarioActualId, Rol rolActual) {
        IntentoCuestionario intento = buscarPorId(intentoId);
        Cuestionario cuestionario = cuestionarioService.buscarPorId(intento.getCuestionarioId());
        cuestionarioService.validarPropietarioDelModulo(cuestionario.getModuloId(), usuarioActualId, rolActual);

        Pregunta pregunta = preguntaRepositorio.buscarPorId(preguntaId)
                .filter(p -> p.getCuestionarioId().equals(cuestionario.getId()))
                .orElseThrow(() -> new PreguntaNoEncontradaException(preguntaId));
        if (pregunta.getTipo() != TipoPregunta.ABIERTA) {
            throw new ReglaNegocioException("Solo las preguntas abiertas se califican a mano");
        }
        if (puntaje > pregunta.getPuntaje()) {
            throw new ReglaNegocioException("El puntaje debe estar entre 0 y " + pregunta.getPuntaje());
        }

        RespuestaValor respuesta = intento.getRespuestas().stream()
                .filter(r -> r.preguntaId().equals(preguntaId))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException(
                        "El estudiante no respondió esta pregunta: cuenta como 0 y no hace falta calificarla"));

        double anterior = respuesta.puntajeObtenido() != null ? respuesta.puntajeObtenido() : 0;
        double nuevaNota = redondear(intento.getNota() - anterior + puntaje);

        boolean quedanPorRevisar = intento.getRespuestas().stream()
                .anyMatch(r -> r.texto() != null && r.puntajeObtenido() == null && !r.preguntaId().equals(preguntaId));
        EstadoIntento estado = quedanPorRevisar ? EstadoIntento.PENDIENTE_REVISION : EstadoIntento.CALIFICADO;

        return intentoCuestionarioRepositorio.calificarRespuesta(intentoId, preguntaId, puntaje, nuevaNota, estado);
    }

    /** Detalle de un intento para el profesor dueño del curso (o el admin), con las respuestas del estudiante. */
    public IntentoCuestionario obtenerParaRevision(Long intentoId, Long usuarioActualId, Rol rolActual) {
        IntentoCuestionario intento = buscarPorId(intentoId);
        Cuestionario cuestionario = cuestionarioService.buscarPorId(intento.getCuestionarioId());
        cuestionarioService.validarPropietarioDelModulo(cuestionario.getModuloId(), usuarioActualId, rolActual);
        return intento;
    }

    public IntentoCuestionario obtenerMiIntento(Long cuestionarioId, Long estudianteId) {
        return intentoCuestionarioRepositorio.buscarPorCuestionarioYEstudiante(cuestionarioId, estudianteId)
                .orElse(null);
    }

    public List<IntentoCuestionario> listarPorCuestionario(Long cuestionarioId, Long usuarioActualId, Rol rolActual) {
        Cuestionario cuestionario = cuestionarioService.buscarPorId(cuestionarioId);
        cuestionarioService.validarPropietarioDelModulo(cuestionario.getModuloId(), usuarioActualId, rolActual);
        return intentoCuestionarioRepositorio.listarPorCuestionario(cuestionarioId);
    }

    private IntentoCuestionario buscarPorId(Long intentoId) {
        return intentoCuestionarioRepositorio.buscarPorId(intentoId)
                .orElseThrow(() -> new IntentoNoEncontradoException(intentoId));
    }

    private void validarMatriculado(Cuestionario cuestionario, Long estudianteId) {
        Modulo modulo = moduloService.buscarPorId(cuestionario.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        if (!matriculaRepositorio.existePorCursoYEstudiante(curso.getId(), estudianteId)) {
            throw new AccessDeniedException("No estás matriculado en este curso");
        }
    }

    /** Evita arrastrar errores de punto flotante al sumar y restar decimales (ej. 3.7 - 0.1 + 0.3). */
    private static double redondear(double valor) {
        return Math.round(valor * 100) / 100.0;
    }
}
