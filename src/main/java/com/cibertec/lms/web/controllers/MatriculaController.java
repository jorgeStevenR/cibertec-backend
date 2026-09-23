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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.matricula.MatriculaService;
import com.cibertec.lms.dominio.matricula.Matricula;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.MatriculaRequestDTO;
import com.cibertec.lms.web.dtos.MatriculaResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final UsuarioRepositorio usuarioRepositorio;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MatriculaResponseDTO> matricular(@Valid @RequestBody MatriculaRequestDTO request) {
        Matricula creada = matriculaService.matricular(request.cursoId(), request.estudianteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(MatriculaResponseDTO.desde(creada, null));
    }

    @GetMapping("/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public List<MatriculaResponseDTO> listarPorEstudiante(
            @PathVariable Long estudianteId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return matriculaService.listarPorEstudiante(estudianteId, actual.id(), actual.rol())
                .stream().map(m -> MatriculaResponseDTO.desde(m, null)).toList();
    }

    /** Admin/profesor ven los participantes de cualquiera de sus cursos; un estudiante solo
     *  los de un curso en el que el mismo esta matriculado (ver MatriculaService). */
    @GetMapping("/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR', 'ESTUDIANTE')")
    public List<MatriculaResponseDTO> listarPorCurso(
            @PathVariable Long cursoId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return matriculaService.listarPorCurso(cursoId, actual.id(), actual.rol())
                .stream()
                .map(m -> MatriculaResponseDTO.desde(m, usuarioRepositorio.buscarPorId(m.getEstudianteId()).orElse(null)))
                .toList();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        matriculaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Llamado por el frontend al entrar a la vista del curso; alimenta la métrica de "último acceso" que ve el profesor. */
    @PostMapping("/curso/{cursoId}/registrar-acceso")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<Void> registrarAcceso(
            @PathVariable Long cursoId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        matriculaService.registrarAcceso(cursoId, actual.id());
        return ResponseEntity.noContent().build();
    }
}
