package com.cibertec.lms.web.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.cuestionario.PreguntaService;
import com.cibertec.lms.dominio.cuestionario.Opcion;
import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.OpcionRequestDTO;
import com.cibertec.lms.web.dtos.PreguntaRequestDTO;
import com.cibertec.lms.web.dtos.PreguntaResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PreguntaController {

    private final PreguntaService preguntaService;

    @PostMapping("/api/cuestionarios/{cuestionarioId}/preguntas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<PreguntaResponseDTO> crear(
            @PathVariable Long cuestionarioId,
            @Valid @RequestBody PreguntaRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Pregunta creada = preguntaService.crear(
                cuestionarioId, request.enunciado(), request.puntaje(), aOpciones(request.opciones()),
                actual.id(), actual.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(PreguntaResponseDTO.desde(creada, true));
    }

    @PutMapping("/api/preguntas/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public PreguntaResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PreguntaRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Pregunta actualizada = preguntaService.actualizar(
                id, request.enunciado(), request.puntaje(), aOpciones(request.opciones()),
                actual.id(), actual.rol());
        return PreguntaResponseDTO.desde(actualizada, true);
    }

    @DeleteMapping("/api/preguntas/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        preguntaService.eliminar(id, actual.id(), actual.rol());
        return ResponseEntity.noContent().build();
    }

    private List<Opcion> aOpciones(List<OpcionRequestDTO> opciones) {
        return opciones.stream()
                .map(o -> Opcion.builder().id(o.id()).texto(o.texto()).esCorrecta(o.esCorrecta()).build())
                .toList();
    }
}
