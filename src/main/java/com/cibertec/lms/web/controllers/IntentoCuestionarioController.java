package com.cibertec.lms.web.controllers;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.cuestionario.IntentoCuestionarioService;
import com.cibertec.lms.aplicacion.cuestionario.PreguntaService;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;
import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.dominio.cuestionario.RespuestaValor;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.CalificarRespuestaRequestDTO;
import com.cibertec.lms.web.dtos.IntentoRequestDTO;
import com.cibertec.lms.web.dtos.IntentoResponseDTO;
import com.cibertec.lms.web.dtos.IntentoResumenResponseDTO;
import com.cibertec.lms.web.dtos.PreguntaConRespuestaResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class IntentoCuestionarioController {

    private final IntentoCuestionarioService intentoCuestionarioService;
    private final PreguntaService preguntaService;
    private final UsuarioRepositorio usuarioRepositorio;

    @PostMapping("/api/cuestionarios/{cuestionarioId}/intentos")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<IntentoResponseDTO> responder(
            @PathVariable Long cuestionarioId,
            @Valid @RequestBody IntentoRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        List<RespuestaValor> respuestas = request.respuestas().stream()
                .map(r -> new RespuestaValor(r.preguntaId(), r.opcionId(), r.texto(), null))
                .toList();
        IntentoCuestionario creado = intentoCuestionarioService.responder(cuestionarioId, respuestas, actual.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(aDto(creado, true));
    }

    @GetMapping("/api/cuestionarios/{cuestionarioId}/mi-intento")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<IntentoResponseDTO> obtenerMiIntento(
            @PathVariable Long cuestionarioId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        IntentoCuestionario intento = intentoCuestionarioService.obtenerMiIntento(cuestionarioId, actual.id());
        if (intento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(aDto(intento, true));
    }

    @GetMapping("/api/cuestionarios/{cuestionarioId}/intentos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public List<IntentoResumenResponseDTO> listarPorCuestionario(
            @PathVariable Long cuestionarioId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        List<IntentoCuestionario> intentos = intentoCuestionarioService.listarPorCuestionario(cuestionarioId, actual.id(), actual.rol());
        Map<Long, Usuario> estudiantes = usuarioRepositorio.buscarPorIds(
                intentos.stream().map(IntentoCuestionario::getEstudianteId).distinct().toList());
        return intentos.stream()
                .map(i -> IntentoResumenResponseDTO.desde(i, estudiantes.get(i.getEstudianteId())))
                .toList();
    }

    /** Detalle de un intento con todas sus respuestas, para que el profesor revise y califique las abiertas. */
    @GetMapping("/api/intentos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public IntentoResponseDTO obtenerParaRevision(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return aDto(intentoCuestionarioService.obtenerParaRevision(id, actual.id(), actual.rol()), false);
    }

    @PatchMapping("/api/intentos/{intentoId}/preguntas/{preguntaId}/calificar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public IntentoResponseDTO calificarRespuesta(
            @PathVariable Long intentoId,
            @PathVariable Long preguntaId,
            @Valid @RequestBody CalificarRespuestaRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        IntentoCuestionario calificado = intentoCuestionarioService.calificarRespuesta(
                intentoId, preguntaId, request.puntaje(), actual.id(), actual.rol());
        return aDto(calificado, false);
    }

    /** {@code ocultarNotaParcial}: true en las respuestas al estudiante, que no ve la nota hasta que se califiquen sus abiertas. */
    private IntentoResponseDTO aDto(IntentoCuestionario intento, boolean ocultarNotaParcial) {
        Usuario estudiante = usuarioRepositorio.buscarPorId(intento.getEstudianteId()).orElse(null);
        Map<Long, RespuestaValor> respuestaPorPregunta = intento.getRespuestas().stream()
                .collect(Collectors.toMap(RespuestaValor::preguntaId, Function.identity()));
        List<Pregunta> preguntas = preguntaService.listarPorCuestionario(intento.getCuestionarioId());
        List<PreguntaConRespuestaResponseDTO> preguntasDto = preguntas.stream()
                .map(p -> PreguntaConRespuestaResponseDTO.desde(p, respuestaPorPregunta.get(p.getId())))
                .toList();
        return IntentoResponseDTO.desde(intento, estudiante, preguntasDto, ocultarNotaParcial);
    }
}
