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

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MatriculaResponseDTO> matricular(@Valid @RequestBody MatriculaRequestDTO request) {
        Matricula creada = matriculaService.matricular(request.cursoId(), request.estudianteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(MatriculaResponseDTO.desde(creada));
    }

    @GetMapping("/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public List<MatriculaResponseDTO> listarPorEstudiante(
            @PathVariable Long estudianteId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return matriculaService.listarPorEstudiante(estudianteId, actual.id(), actual.rol())
                .stream().map(MatriculaResponseDTO::desde).toList();
    }

    @GetMapping("/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public List<MatriculaResponseDTO> listarPorCurso(
            @PathVariable Long cursoId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return matriculaService.listarPorCurso(cursoId, actual.id(), actual.rol())
                .stream().map(MatriculaResponseDTO::desde).toList();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        matriculaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
