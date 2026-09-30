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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cibertec.lms.aplicacion.curso.BloqueService;
import com.cibertec.lms.dominio.curso.BloqueContenido;
import com.cibertec.lms.dominio.curso.TipoBloque;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.BloqueResponseDTO;
import com.cibertec.lms.web.dtos.BloqueUrlResponseDTO;
import com.cibertec.lms.web.dtos.ReordenarBloquesRequestDTO;
import com.cibertec.lms.web.dtos.TextoBloqueRequestDTO;
import com.cibertec.lms.web.dtos.VideoBloqueRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BloqueController {

    private final BloqueService bloqueService;

    @PostMapping("/api/modulos/{moduloId}/bloques/texto")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<BloqueResponseDTO> crearTexto(
            @PathVariable Long moduloId,
            @Valid @RequestBody TextoBloqueRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        BloqueContenido creado = bloqueService.crearTexto(moduloId, request.texto(), actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(BloqueResponseDTO.desde(creado));
    }

    @PostMapping("/api/modulos/{moduloId}/bloques/video")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<BloqueResponseDTO> crearVideo(
            @PathVariable Long moduloId,
            @Valid @RequestBody VideoBloqueRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        BloqueContenido creado = bloqueService.crearVideo(moduloId, request.urlVideo(), actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(BloqueResponseDTO.desde(creado));
    }

    @PostMapping("/api/modulos/{moduloId}/bloques/archivo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<BloqueResponseDTO> subirArchivo(
            @PathVariable Long moduloId,
            @RequestParam TipoBloque tipo,
            @RequestParam("archivo") MultipartFile archivo,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        try {
            BloqueContenido creado = bloqueService.subirArchivo(
                    moduloId,
                    tipo,
                    archivo.getOriginalFilename(),
                    archivo.getInputStream(),
                    archivo.getSize(),
                    archivo.getContentType(),
                    actual.id(),
                    actual.rol());
            return ResponseEntity.status(HttpStatus.CREATED).body(BloqueResponseDTO.desde(creado));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo subido", e);
        }
    }

    @GetMapping("/api/modulos/{moduloId}/bloques")
    public List<BloqueResponseDTO> listarPorModulo(@PathVariable Long moduloId) {
        return bloqueService.listarPorModulo(moduloId).stream().map(BloqueResponseDTO::desde).toList();
    }

    @PostMapping("/api/paginas/{paginaId}/bloques/texto")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<BloqueResponseDTO> crearTextoEnPagina(
            @PathVariable Long paginaId,
            @Valid @RequestBody TextoBloqueRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        BloqueContenido creado = bloqueService.crearTextoEnPagina(paginaId, request.texto(), actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(BloqueResponseDTO.desde(creado));
    }

    @PostMapping("/api/paginas/{paginaId}/bloques/video")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<BloqueResponseDTO> crearVideoEnPagina(
            @PathVariable Long paginaId,
            @Valid @RequestBody VideoBloqueRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        BloqueContenido creado = bloqueService.crearVideoEnPagina(paginaId, request.urlVideo(), actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(BloqueResponseDTO.desde(creado));
    }

    @PostMapping("/api/paginas/{paginaId}/bloques/archivo")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<BloqueResponseDTO> subirArchivoEnPagina(
            @PathVariable Long paginaId,
            @RequestParam TipoBloque tipo,
            @RequestParam("archivo") MultipartFile archivo,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        try {
            BloqueContenido creado = bloqueService.subirArchivoEnPagina(
                    paginaId,
                    tipo,
                    archivo.getOriginalFilename(),
                    archivo.getInputStream(),
                    archivo.getSize(),
                    archivo.getContentType(),
                    actual.id(),
                    actual.rol());
            return ResponseEntity.status(HttpStatus.CREATED).body(BloqueResponseDTO.desde(creado));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo subido", e);
        }
    }

    @GetMapping("/api/paginas/{paginaId}/bloques")
    public List<BloqueResponseDTO> listarPorPagina(
            @PathVariable Long paginaId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return bloqueService.listarPorPagina(paginaId, actual.id(), actual.rol()).stream()
                .map(BloqueResponseDTO::desde)
                .toList();
    }

    @PatchMapping("/api/paginas/{paginaId}/bloques/orden")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<Void> reordenarEnPagina(
            @PathVariable Long paginaId,
            @Valid @RequestBody ReordenarBloquesRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        bloqueService.reordenarEnPagina(paginaId, request.ids(), actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/bloques/{id}/texto")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public BloqueResponseDTO actualizarTexto(
            @PathVariable Long id,
            @Valid @RequestBody TextoBloqueRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return BloqueResponseDTO.desde(
                bloqueService.actualizarTexto(id, request.texto(), actual.id(), actual.rol()));
    }

    @PatchMapping("/api/modulos/{moduloId}/bloques/orden")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<Void> reordenar(
            @PathVariable Long moduloId,
            @Valid @RequestBody ReordenarBloquesRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        bloqueService.reordenar(moduloId, request.ids(), actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/bloques/{id}/url")
    public BloqueUrlResponseDTO obtenerUrlDescarga(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return new BloqueUrlResponseDTO(bloqueService.obtenerUrlDescarga(id, actual.id(), actual.rol()));
    }

    @DeleteMapping("/api/bloques/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        bloqueService.eliminar(id, actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }
}
