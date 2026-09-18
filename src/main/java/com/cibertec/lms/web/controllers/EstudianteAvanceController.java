package com.cibertec.lms.web.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.estudiante.AvanceEstudianteService;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.CalificacionesCursoResponseDTO;
import com.cibertec.lms.web.dtos.ResumenCursoResponseDTO;
import com.cibertec.lms.web.dtos.VencimientoResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/estudiantes/{estudianteId}")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
public class EstudianteAvanceController {

    private final AvanceEstudianteService avanceService;

    @GetMapping("/cursos/resumen")
    public List<ResumenCursoResponseDTO> resumenPorCurso(
            @PathVariable Long estudianteId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return avanceService.resumenPorCurso(estudianteId, actual.id(), actual.rol()).stream()
                .map(ResumenCursoResponseDTO::desde)
                .toList();
    }

    @GetMapping("/vencimientos")
    public List<VencimientoResponseDTO> vencimientos(
            @PathVariable Long estudianteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return avanceService.vencimientos(estudianteId, desde, hasta, actual.id(), actual.rol()).stream()
                .map(VencimientoResponseDTO::desde)
                .toList();
    }

    @GetMapping("/calificaciones")
    public List<CalificacionesCursoResponseDTO> calificaciones(
            @PathVariable Long estudianteId,
            @RequestParam(required = false) Long cursoId,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        return avanceService.calificaciones(estudianteId, cursoId, actual.id(), actual.rol()).stream()
                .map(CalificacionesCursoResponseDTO::desde)
                .toList();
    }
}
