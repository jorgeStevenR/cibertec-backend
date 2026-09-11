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

import com.cibertec.lms.aplicacion.actividad.ActividadService;
import com.cibertec.lms.dominio.actividad.Actividad;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.ActividadRequestDTO;
import com.cibertec.lms.web.dtos.ActividadResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ActividadController {

    private final ActividadService actividadService;

    @PostMapping("/api/modulos/{moduloId}/actividades")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<ActividadResponseDTO> crear(
            @PathVariable Long moduloId,
            @Valid @RequestBody ActividadRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Actividad creada = actividadService.crear(
                moduloId, request.titulo(), request.descripcion(), request.fechaEntrega(),
                request.puntajeMaximo(), actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(ActividadResponseDTO.desde(creada));
    }

    @GetMapping("/api/modulos/{moduloId}/actividades")
    public List<ActividadResponseDTO> listarPorModulo(@PathVariable Long moduloId) {
        return actividadService.listarPorModulo(moduloId).stream().map(ActividadResponseDTO::desde).toList();
    }

    @GetMapping("/api/actividades/{id}")
    public ActividadResponseDTO buscarPorId(@PathVariable Long id) {
        return ActividadResponseDTO.desde(actividadService.buscarPorId(id));
    }

    @PutMapping("/api/actividades/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ActividadResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActividadRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Actividad actualizada = actividadService.actualizar(
                id, request.titulo(), request.descripcion(), request.fechaEntrega(),
                request.puntajeMaximo(), actual.id(), actual.rol());
        return ActividadResponseDTO.desde(actualizada);
    }

    @DeleteMapping("/api/actividades/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        actividadService.eliminar(id, actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }
}
