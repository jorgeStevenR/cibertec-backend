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

import com.cibertec.lms.aplicacion.matricula.MatriculaProgramaService;
import com.cibertec.lms.dominio.matricula.MatriculaPrograma;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.MatriculaProgramaRequestDTO;
import com.cibertec.lms.web.dtos.MatriculaProgramaResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/matriculas-programa")
@RequiredArgsConstructor
public class MatriculaProgramaController {

    private final MatriculaProgramaService matriculaProgramaService;
    private final UsuarioRepositorio usuarioRepositorio;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MatriculaProgramaResponseDTO> matricular(@Valid @RequestBody MatriculaProgramaRequestDTO request) {
        MatriculaPrograma creada = matriculaProgramaService.matricular(request.programaId(), request.estudianteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(MatriculaProgramaResponseDTO.desde(creada, null));
    }

    @GetMapping("/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public List<MatriculaProgramaResponseDTO> listarPorEstudiante(
            @PathVariable Long estudianteId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return matriculaProgramaService.listarPorEstudiante(estudianteId, actual.id(), actual.rol())
                .stream().map(m -> MatriculaProgramaResponseDTO.desde(m, null)).toList();
    }

    /** No hay noción de "profesor dueño de un programa" (a diferencia de curso), así que el roster completo
     *  de un programa queda como dato administrativo: solo ADMINISTRADOR. */
    @GetMapping("/programa/{programaId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<MatriculaProgramaResponseDTO> listarPorPrograma(@PathVariable Long programaId) {
        return matriculaProgramaService.listarPorPrograma(programaId)
                .stream()
                .map(m -> MatriculaProgramaResponseDTO.desde(m, usuarioRepositorio.buscarPorId(m.getEstudianteId()).orElse(null)))
                .toList();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        matriculaProgramaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
