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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cibertec.lms.aplicacion.curso.LienzoService;
import com.cibertec.lms.dominio.curso.Lienzo;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.ImagenSubidaResponseDTO;
import com.cibertec.lms.web.dtos.LienzoContenidoRequestDTO;
import com.cibertec.lms.web.dtos.LienzoContenidoResponseDTO;
import com.cibertec.lms.web.dtos.LienzoResumenDTO;
import com.cibertec.lms.web.dtos.NombreLienzoRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LienzoController {

    private final LienzoService lienzoService;

    @GetMapping("/api/modulos/{moduloId}/lienzos")
    public List<LienzoResumenDTO> listar(
            @PathVariable Long moduloId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return lienzoService.listar(moduloId, actual.id(), actual.rol())
                .stream().map(LienzoResumenDTO::desde).toList();
    }

    @PostMapping("/api/modulos/{moduloId}/lienzos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<LienzoResumenDTO> crear(
            @PathVariable Long moduloId,
            @Valid @RequestBody NombreLienzoRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Lienzo creado = lienzoService.crear(moduloId, request.nombre(), actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(LienzoResumenDTO.desde(creado));
    }

    @PutMapping("/api/lienzos/{lienzoId}/nombre")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public LienzoResumenDTO renombrar(
            @PathVariable Long lienzoId,
            @Valid @RequestBody NombreLienzoRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return LienzoResumenDTO.desde(
                lienzoService.renombrar(lienzoId, request.nombre(), actual.id(), actual.rol()));
    }

    @DeleteMapping("/api/lienzos/{lienzoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long lienzoId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        lienzoService.eliminar(lienzoId, actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/lienzos/{lienzoId}")
    public LienzoContenidoResponseDTO obtenerContenido(
            @PathVariable Long lienzoId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return LienzoContenidoResponseDTO.desde(
                lienzoService.obtenerContenido(lienzoId, actual.id(), actual.rol()));
    }

    @PutMapping("/api/lienzos/{lienzoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public LienzoContenidoResponseDTO guardarContenido(
            @PathVariable Long lienzoId,
            @Valid @RequestBody LienzoContenidoRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Lienzo guardado = lienzoService.guardarContenido(
                lienzoId, request.ancho(), request.alto(), request.elementos(), actual.id(), actual.rol());
        return LienzoContenidoResponseDTO.desde(guardado);
    }

    @PostMapping("/api/lienzos/{lienzoId}/imagenes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<ImagenSubidaResponseDTO> subirImagen(
            @PathVariable Long lienzoId,
            @RequestParam("archivo") MultipartFile archivo,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        try {
            String url = lienzoService.subirImagen(
                    lienzoId,
                    archivo.getOriginalFilename(),
                    archivo.getInputStream(),
                    archivo.getSize(),
                    archivo.getContentType(),
                    actual.id(),
                    actual.rol());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ImagenSubidaResponseDTO(url));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo subido", e);
        }
    }
}
