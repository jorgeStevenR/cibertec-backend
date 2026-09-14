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
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionarioRepositorio;
import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.dominio.cuestionario.PreguntaRepositorio;
import com.cibertec.lms.dominio.cuestionario.RespuestaValor;
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

    /** Corrección automática: por cada pregunta del cuestionario se busca la opción que el estudiante
     *  eligió (si eligió alguna) y, si coincide con la marcada esCorrecta=true, suma su puntaje. Una
     *  respuesta ausente, o que apunte a una pregunta/opción que no pertenece a este cuestionario, sencillamente
     *  no suma - el efecto neto es el mismo que responder mal, así que no hace falta rechazarla con un error. */
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
        Map<Long, Long> opcionElegidaPorPregunta = respuestasEnviadas.stream()
                .collect(Collectors.toMap(RespuestaValor::preguntaId, RespuestaValor::opcionId, (a, b) -> b));

        double nota = 0;
        double puntajeMaximo = 0;
        List<RespuestaValor> respuestasValidas = new ArrayList<>();

        for (Pregunta pregunta : preguntas) {
            puntajeMaximo += pregunta.getPuntaje();
            Long opcionElegidaId = opcionElegidaPorPregunta.get(pregunta.getId());
            if (opcionElegidaId == null) {
                continue;
            }
            respuestasValidas.add(new RespuestaValor(pregunta.getId(), opcionElegidaId));

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
                .fechaEntrega(LocalDateTime.now())
                .respuestas(respuestasValidas)
                .build();

        return intentoCuestionarioRepositorio.guardar(nuevo);
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

    private void validarMatriculado(Cuestionario cuestionario, Long estudianteId) {
        Modulo modulo = moduloService.buscarPorId(cuestionario.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        if (!matriculaRepositorio.existePorCursoYEstudiante(curso.getId(), estudianteId)) {
            throw new AccessDeniedException("No estás matriculado en este curso");
        }
    }
}
