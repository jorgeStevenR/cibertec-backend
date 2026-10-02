package com.cibertec.lms.web.controllers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cibertec.lms.aplicacion.actividad.EntregaService;
import com.cibertec.lms.dominio.actividad.Entrega;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.CalificarRequestDTO;
import com.cibertec.lms.web.dtos.ContenidoUrlResponseDTO;
import com.cibertec.lms.web.dtos.EntregaResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaService entregaService;
    private final UsuarioRepositorio usuarioRepositorio;

    @PostMapping("/api/actividades/{actividadId}/entregas")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<EntregaResponseDTO> entregar(
            @PathVariable Long actividadId,
            @RequestParam("archivo") MultipartFile archivo,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        try {
            Entrega creada = entregaService.entregar(
                    actividadId, archivo.getOriginalFilename(), archivo.getInputStream(),
                    archivo.getSize(), archivo.getContentType(), actual.id());
            return ResponseEntity.status(HttpStatus.CREATED).body(aDto(creada));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo entregado", e);
        }
    }

    @GetMapping("/api/actividades/{actividadId}/entregas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public List<EntregaResponseDTO> listarPorActividad(
            @PathVariable Long actividadId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        List<Entrega> entregas = entregaService.listarPorActividad(actividadId, actual.id(), actual.rol());
        Map<Long, Usuario> estudiantes = usuarioRepositorio.buscarPorIds(
                entregas.stream().map(Entrega::getEstudianteId).distinct().toList());
        return entregas.stream()
                .map(e -> EntregaResponseDTO.desde(e, estudiantes.get(e.getEstudianteId())))
                .toList();
    }

    @GetMapping("/api/actividades/{actividadId}/entregas/mia")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<EntregaResponseDTO> obtenerMiEntrega(
            @PathVariable Long actividadId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Entrega entrega = entregaService.obtenerMiEntrega(actividadId, actual.id());
        if (entrega == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(aDto(entrega));
    }

    @PatchMapping("/api/entregas/{id}/calificar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public EntregaResponseDTO calificar(
            @PathVariable Long id,
            @Valid @RequestBody CalificarRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Entrega calificada = entregaService.calificar(
                id, request.nota(), request.comentario(), actual.id(), actual.rol());
        return aDto(calificada);
    }

    @GetMapping("/api/entregas/{id}/url")
    public ContenidoUrlResponseDTO obtenerUrlDescarga(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return new ContenidoUrlResponseDTO(entregaService.obtenerUrlDescarga(id, actual.id(), actual.rol()));
    }

    private EntregaResponseDTO aDto(Entrega entrega) {
        Usuario estudiante = usuarioRepositorio.buscarPorId(entrega.getEstudianteId()).orElse(null);
        return EntregaResponseDTO.desde(entrega, estudiante);
    }
}
