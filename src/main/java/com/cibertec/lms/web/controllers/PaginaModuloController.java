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

import com.cibertec.lms.aplicacion.curso.PaginaModuloService;
import com.cibertec.lms.dominio.curso.PaginaModulo;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.PaginaModuloRequestDTO;
import com.cibertec.lms.web.dtos.PaginaModuloResponseDTO;
import com.cibertec.lms.web.dtos.ReordenarPaginasRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PaginaModuloController {

    private final PaginaModuloService paginaModuloService;

    @GetMapping("/api/modulos/{moduloId}/paginas")
    public List<PaginaModuloResponseDTO> listarPorModulo(
            @PathVariable Long moduloId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return paginaModuloService.listarPorModulo(moduloId, actual.id(), actual.rol()).stream()
                .map(PaginaModuloResponseDTO::desde)
                .toList();
    }

    @PostMapping("/api/modulos/{moduloId}/paginas")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<PaginaModuloResponseDTO> crear(
            @PathVariable Long moduloId,
            @Valid @RequestBody PaginaModuloRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        PaginaModulo creada = paginaModuloService.crear(moduloId, request.titulo(), actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(PaginaModuloResponseDTO.desde(creada));
    }

    @PatchMapping("/api/modulos/{moduloId}/paginas/orden")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<Void> reordenar(
            @PathVariable Long moduloId,
            @Valid @RequestBody ReordenarPaginasRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        paginaModuloService.reordenar(moduloId, request.ids(), actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/paginas/{id}")
    public PaginaModuloResponseDTO buscarPorId(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        return PaginaModuloResponseDTO.desde(paginaModuloService.buscarPorId(id, actual.id(), actual.rol()));
    }

    @PutMapping("/api/paginas/{id}")
    @PreAuthorize("hasRole('PROFESOR')")
    public PaginaModuloResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PaginaModuloRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return PaginaModuloResponseDTO.desde(paginaModuloService.actualizar(id, request.titulo(), actual.id(), actual.rol()));
    }

    @DeleteMapping("/api/paginas/{id}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        paginaModuloService.eliminar(id, actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }
}
