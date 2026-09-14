package com.cibertec.lms.web.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.cuestionario.CuestionarioService;
import com.cibertec.lms.aplicacion.cuestionario.PreguntaService;
import com.cibertec.lms.dominio.cuestionario.Cuestionario;
import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.CuestionarioRequestDTO;
import com.cibertec.lms.web.dtos.CuestionarioResponseDTO;
import com.cibertec.lms.web.dtos.PreguntaResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CuestionarioController {

    private final CuestionarioService cuestionarioService;
    private final PreguntaService preguntaService;

    @PostMapping("/api/modulos/{moduloId}/cuestionarios")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<CuestionarioResponseDTO> crear(
            @PathVariable Long moduloId,
            @Valid @RequestBody CuestionarioRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Cuestionario creado = cuestionarioService.crear(
                moduloId, request.titulo(), request.descripcion(), request.fechaApertura(), request.fechaEntrega(),
                request.tiempoLimiteMinutos(), actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CuestionarioResponseDTO.resumen(creado, cuestionarioService.estaDisponible(creado), 0));
    }

    @GetMapping("/api/modulos/{moduloId}/cuestionarios")
    public List<CuestionarioResponseDTO> listarPorModulo(
            @PathVariable Long moduloId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return cuestionarioService.listarPorModulo(moduloId, actual.id(), actual.rol()).stream()
                .map(c -> CuestionarioResponseDTO.resumen(
                        c, cuestionarioService.estaDisponible(c), cuestionarioService.calcularPuntajeMaximo(c.getId())))
                .toList();
    }

    @GetMapping("/api/cuestionarios/{id}")
    public CuestionarioResponseDTO buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        // buscarPorModulo ya valida el acceso (dueño del curso o estudiante matriculado); reusamos la
        // lista para no duplicar la validación, y de paso confirmamos que el cuestionario es de ese módulo.
        Cuestionario cuestionario = cuestionarioService.buscarPorId(id);
        cuestionarioService.listarPorModulo(cuestionario.getModuloId(), actual.id(), actual.rol());

        boolean disponible = cuestionarioService.estaDisponible(cuestionario);

        // Un estudiante no puede ver las preguntas antes de que abra (para que no las espíe antes de
        // tiempo); dueño del curso/admin siempre las ve completas, y un estudiante después de que abre
        // también - incluso si ya venció, porque esa fecha solo restringe el envío, no la lectura.
        boolean ocultarPreguntas = actual.rol() == Rol.ESTUDIANTE && cuestionarioService.aunNoAbre(cuestionario);
        boolean revelarCorrecta = actual.rol() != Rol.ESTUDIANTE;

        List<PreguntaResponseDTO> preguntas = ocultarPreguntas
                ? List.of()
                : preguntaService.listarPorCuestionario(id).stream()
                        .map(p -> PreguntaResponseDTO.desde(p, revelarCorrecta))
                        .toList();
        double puntajeMaximo = cuestionarioService.calcularPuntajeMaximo(id);

        return CuestionarioResponseDTO.detalle(cuestionario, disponible, puntajeMaximo, preguntas);
    }

    @PutMapping("/api/cuestionarios/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public CuestionarioResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CuestionarioRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Cuestionario actualizado = cuestionarioService.actualizar(
                id, request.titulo(), request.descripcion(), request.fechaApertura(), request.fechaEntrega(),
                request.tiempoLimiteMinutos(), actual.id(), actual.rol());
        return CuestionarioResponseDTO.resumen(
                actualizado, cuestionarioService.estaDisponible(actualizado), cuestionarioService.calcularPuntajeMaximo(id));
    }

    @DeleteMapping("/api/cuestionarios/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        cuestionarioService.eliminar(id, actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }
}
