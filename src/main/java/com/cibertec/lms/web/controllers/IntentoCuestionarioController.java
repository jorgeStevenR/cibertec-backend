package com.cibertec.lms.web.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
                .map(r -> new RespuestaValor(r.preguntaId(), r.opcionId()))
                .toList();
        IntentoCuestionario creado = intentoCuestionarioService.responder(cuestionarioId, respuestas, actual.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(aDto(creado));
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
        return ResponseEntity.ok(aDto(intento));
    }

    @GetMapping("/api/cuestionarios/{cuestionarioId}/intentos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public List<IntentoResumenResponseDTO> listarPorCuestionario(
            @PathVariable Long cuestionarioId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return intentoCuestionarioService.listarPorCuestionario(cuestionarioId, actual.id(), actual.rol()).stream()
                .map(i -> IntentoResumenResponseDTO.desde(i, usuarioRepositorio.buscarPorId(i.getEstudianteId()).orElse(null)))
                .toList();
    }

    private IntentoResponseDTO aDto(IntentoCuestionario intento) {
        Usuario estudiante = usuarioRepositorio.buscarPorId(intento.getEstudianteId()).orElse(null);

        Map<Long, Long> opcionElegidaPorPregunta = intento.getRespuestas().stream()
                .collect(Collectors.toMap(RespuestaValor::preguntaId, RespuestaValor::opcionId));

        List<Pregunta> preguntas = preguntaService.listarPorCuestionario(intento.getCuestionarioId());
        List<PreguntaConRespuestaResponseDTO> preguntasDto = preguntas.stream()
                .map(p -> PreguntaConRespuestaResponseDTO.desde(p, opcionElegidaPorPregunta.get(p.getId())))
                .toList();

        return IntentoResponseDTO.desde(intento, estudiante, preguntasDto);
    }
}
