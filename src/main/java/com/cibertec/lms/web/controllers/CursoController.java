package com.cibertec.lms.web.controllers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.lms.aplicacion.curso.ActualizarCursoComando;
import com.cibertec.lms.aplicacion.curso.CrearCursoComando;
import com.cibertec.lms.aplicacion.curso.CursoService;
import com.cibertec.lms.dominio.comun.Pagina;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.programa.Programa;
import com.cibertec.lms.dominio.programa.ProgramaRepositorio;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;
import com.cibertec.lms.web.dtos.CursoActualizarRequestDTO;
import com.cibertec.lms.web.dtos.CursoRequestDTO;
import com.cibertec.lms.web.dtos.CursoResponseDTO;
import com.cibertec.lms.web.dtos.PaginaResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ProgramaRepositorio programaRepositorio;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CursoResponseDTO> crear(@Valid @RequestBody CursoRequestDTO request) {
        Curso creado = cursoService.crear(new CrearCursoComando(
                request.titulo(), request.descripcion(), request.profesorId(), request.programaId(),
                request.codigo(), request.periodo(), request.cupoMaximo(), request.fechaInicio(), request.fechaFin()));
        return ResponseEntity.status(HttpStatus.CREATED).body(aDto(creado, 0));
    }

    @GetMapping
    public PaginaResponseDTO<CursoResponseDTO> listar(
            @RequestParam(required = false) Long profesorId,
            @RequestParam(required = false) Long programaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {

        Pagina<Curso> resultado = cursoService.listarPaginado(pagina, tamano, profesorId, programaId);
        List<Curso> cursos = resultado.contenido();

        // Una sola consulta agrupada por tipo de entidad para toda la página, no una por curso (evita N+1).
        Map<Long, Integer> matriculados = cursoService.contarMatriculadosPorCurso(cursos.stream().map(Curso::getId).toList());
        Map<Long, Usuario> profesores = usuarioRepositorio.buscarPorIds(
                cursos.stream().map(Curso::getProfesorId).distinct().toList());
        Map<Long, Programa> programas = programaRepositorio.buscarPorIds(
                cursos.stream().map(Curso::getProgramaId).filter(Objects::nonNull).distinct().toList());

        return new PaginaResponseDTO<>(
                cursos.stream()
                        .map(c -> CursoResponseDTO.desde(
                                c,
                                profesores.get(c.getProfesorId()),
                                c.getProgramaId() != null ? programas.get(c.getProgramaId()) : null,
                                matriculados.getOrDefault(c.getId(), 0)))
                        .toList(),
                resultado.pagina(),
                resultado.tamano(),
                resultado.totalElementos(),
                resultado.totalPaginas());
    }

    @GetMapping("/{id}")
    public CursoResponseDTO buscarPorId(@PathVariable Long id) {
        return aDto(cursoService.buscarPorId(id), cursoService.contarMatriculados(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR')")
    public CursoResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CursoActualizarRequestDTO request,
            @AuthenticationPrincipal UsuarioAutenticado actual) {
        Curso actualizado = cursoService.actualizar(
                id,
                new ActualizarCursoComando(
                        request.titulo(), request.descripcion(), request.publicado(), request.programaId(),
                        request.codigo(), request.periodo(), request.cupoMaximo(), request.fechaInicio(), request.fechaFin()),
                actual.id(), actual.rol());
        return aDto(actualizado, cursoService.contarMatriculados(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Ni el profesor ni el programa son datos sensibles (cualquier autenticado ya puede ver el curso), asi
     *  que los resolvemos aqui en vez de exigir el rol ADMINISTRADOR que protege /api/usuarios y /api/programas. */
    private CursoResponseDTO aDto(Curso curso, int matriculados) {
        Usuario profesor = usuarioRepositorio.buscarPorId(curso.getProfesorId()).orElse(null);
        var programa = curso.getProgramaId() != null
                ? programaRepositorio.buscarPorId(curso.getProgramaId()).orElse(null)
                : null;
        return CursoResponseDTO.desde(curso, profesor, programa, matriculados);
    }
}
