package com.cibertec.lms.web.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.curso.ModuloService;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.ModuloRequestDTO;
import com.cibertec.lms.web.dtos.ModuloResponseDTO;
import com.cibertec.lms.web.dtos.ReordenarModulosRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ModuloController {

    private final ModuloService moduloService;

    @PostMapping("/api/cursos/{cursoId}/modulos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<ModuloResponseDTO> crear(
            @PathVariable Long cursoId,
            @Valid @RequestBody ModuloRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Modulo creado = moduloService.crear(
                cursoId, request.titulo(), request.orden(), request.moduloPadreId(), actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(ModuloResponseDTO.desde(creado));
    }

    @GetMapping("/api/cursos/{cursoId}/modulos")
    public List<ModuloResponseDTO> listarPorCurso(@PathVariable Long cursoId) {
        return moduloService.listarPorCurso(cursoId).stream().map(ModuloResponseDTO::desde).toList();
    }

    @GetMapping("/api/modulos/{id}")
    public ModuloResponseDTO buscarPorId(@PathVariable Long id) {
        return ModuloResponseDTO.desde(moduloService.buscarPorId(id));
    }

    @PutMapping("/api/modulos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ModuloResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ModuloRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Modulo actualizado = moduloService.actualizar(
                id, request.titulo(), request.orden(), request.moduloPadreId(), actual.id(), actual.rol());
        return ModuloResponseDTO.desde(actualizado);
    }

    @PatchMapping("/api/cursos/{cursoId}/modulos/orden")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<Void> reordenar(
            @PathVariable Long cursoId,
            @Valid @RequestBody ReordenarModulosRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        moduloService.reordenar(cursoId, request.moduloPadreId(), request.ids(), actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/modulos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        moduloService.eliminar(id, actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }
}
