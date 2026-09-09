package com.cibertec.lms.web.controllers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cibertec.lms.aplicacion.curso.ContenidoService;
import com.cibertec.lms.dominio.curso.ContenidoModulo;
import com.cibertec.lms.dominio.curso.TipoContenido;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.ContenidoResponseDTO;
import com.cibertec.lms.web.dtos.ContenidoUrlResponseDTO;
import com.cibertec.lms.web.dtos.EnlaceRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ContenidoController {

    private final ContenidoService contenidoService;

    @PostMapping("/api/modulos/{moduloId}/contenidos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<ContenidoResponseDTO> subirArchivo(
            @PathVariable Long moduloId,
            @RequestParam TipoContenido tipo,
            @RequestParam("archivo") MultipartFile archivo,
            @AuthenticationPrincipal UsuarioAutenticado actual) {

        try {
            ContenidoModulo creado = contenidoService.subirArchivo(
                    moduloId,
                    tipo,
                    archivo.getOriginalFilename(),
                    archivo.getInputStream(),
                    archivo.getSize(),
                    archivo.getContentType(),
                    actual.id(),
                    actual.rol());
            return ResponseEntity.status(HttpStatus.CREATED).body(ContenidoResponseDTO.desde(creado));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo subido", e);
        }
    }

    @PostMapping("/api/modulos/{moduloId}/contenidos/enlace")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<ContenidoResponseDTO> agregarEnlace(
            @PathVariable Long moduloId,
            @Valid @RequestBody EnlaceRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {

        ContenidoModulo creado = contenidoService.agregarEnlace(
                moduloId, request.nombre(), request.url(), actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(ContenidoResponseDTO.desde(creado));
    }

    @GetMapping("/api/modulos/{moduloId}/contenidos")
    public List<ContenidoResponseDTO> listarPorModulo(
            @PathVariable Long moduloId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return contenidoService.listarPorModulo(moduloId, actual.id(), actual.rol())
                .stream().map(ContenidoResponseDTO::desde).toList();
    }

    @PatchMapping("/api/contenidos/{id}/visibilidad")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ContenidoResponseDTO cambiarVisibilidad(
            @PathVariable Long id,
            @RequestParam boolean visible,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return ContenidoResponseDTO.desde(
                contenidoService.cambiarVisibilidad(id, visible, actual.id(), actual.rol()));
    }

    @GetMapping("/api/contenidos/{id}/url")
    public ContenidoUrlResponseDTO obtenerUrlDescarga(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return new ContenidoUrlResponseDTO(contenidoService.obtenerUrlDescarga(id, actual.id(), actual.rol()));
    }

    @DeleteMapping("/api/contenidos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        contenidoService.eliminar(id, actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }
}
