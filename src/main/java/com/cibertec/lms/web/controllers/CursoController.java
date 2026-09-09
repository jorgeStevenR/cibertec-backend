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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.curso.CursoService;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.CursoActualizarRequestDTO;
import com.cibertec.lms.web.dtos.CursoRequestDTO;
import com.cibertec.lms.web.dtos.CursoResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CursoResponseDTO> crear(@Valid @RequestBody CursoRequestDTO request) {
        Curso creado = cursoService.crear(request.titulo(), request.descripcion(), request.profesorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CursoResponseDTO.desde(creado));
    }

    @GetMapping
    public List<CursoResponseDTO> listar(@RequestParam(required = false) Long profesorId) {
        List<Curso> cursos = profesorId != null
                ? cursoService.listarPorProfesor(profesorId)
                : cursoService.listarTodos();

        return cursos.stream().map(CursoResponseDTO::desde).toList();
    }

    @GetMapping("/{id}")
    public CursoResponseDTO buscarPorId(@PathVariable Long id) {
        return CursoResponseDTO.desde(cursoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public CursoResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CursoActualizarRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Curso actualizado = cursoService.actualizar(
                id, request.titulo(), request.descripcion(), request.publicado(), actual.id(), actual.rol());
        return CursoResponseDTO.desde(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
