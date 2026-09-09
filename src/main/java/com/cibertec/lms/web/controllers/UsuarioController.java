package com.cibertec.lms.web.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.usuario.UsuarioService;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.web.dtos.ResetPasswordResponseDTO;
import com.cibertec.lms.web.dtos.UsuarioActualizarRequestDTO;
import com.cibertec.lms.web.dtos.UsuarioRequestDTO;
import com.cibertec.lms.web.dtos.UsuarioResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO request) {
        Usuario creado = usuarioService.registrar(
                request.nombre(),
                request.apellido(),
                request.email(),
                request.password(),
                request.rol()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponseDTO.desde(creado));
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return usuarioService.listarTodos().stream()
                .map(UsuarioResponseDTO::desde)
                .toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
        return UsuarioResponseDTO.desde(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioActualizarRequestDTO request) {
        Usuario actualizado = usuarioService.actualizar(
                id,
                request.nombre(),
                request.apellido(),
                request.rol(),
                request.activo()
        );
        return UsuarioResponseDTO.desde(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/resetear-password")
    public ResetPasswordResponseDTO resetearPassword(@PathVariable Long id) {
        return new ResetPasswordResponseDTO(usuarioService.resetearPassword(id));
    }
}
