package com.cibertec.lms.web.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.programa.ProgramaService;
import com.cibertec.lms.dominio.comun.Pagina;
import com.cibertec.lms.dominio.programa.Programa;
import com.cibertec.lms.web.dtos.PaginaResponseDTO;
import com.cibertec.lms.web.dtos.ProgramaRequestDTO;
import com.cibertec.lms.web.dtos.ProgramaResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/programas")
@RequiredArgsConstructor
public class ProgramaController {

    private final ProgramaService programaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProgramaResponseDTO> crear(@Valid @RequestBody ProgramaRequestDTO request) {
        Programa creado = programaService.crear(request.nombre(), request.descripcion(), request.duracionSemestres());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProgramaResponseDTO.desde(creado));
    }

    @GetMapping
    public PaginaResponseDTO<ProgramaResponseDTO> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano,
            @RequestParam(required = false) String busqueda) {

        Pagina<Programa> resultado = programaService.listarPaginado(pagina, tamano, busqueda);
        return new PaginaResponseDTO<>(
                resultado.contenido().stream().map(ProgramaResponseDTO::desde).toList(),
                resultado.pagina(),
                resultado.tamano(),
                resultado.totalElementos(),
                resultado.totalPaginas());
    }

    @GetMapping("/{id}")
    public ProgramaResponseDTO buscarPorId(@PathVariable Long id) {
        return ProgramaResponseDTO.desde(programaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ProgramaResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody ProgramaRequestDTO request) {
        Programa actualizado = programaService.actualizar(
                id, request.nombre(), request.descripcion(), request.duracionSemestres());
        return ProgramaResponseDTO.desde(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        programaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
