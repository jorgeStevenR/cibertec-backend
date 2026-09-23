package com.cibertec.lms.aplicacion.matricula;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.lms.aplicacion.curso.CursoService;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.excepcion.MatriculaNoEncontradaException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.matricula.Matricula;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepositorio matriculaRepositorio;
    private final CursoService cursoService;
    private final UsuarioRepositorio usuarioRepositorio;

    /** Transaccional y con el curso bloqueado (FOR UPDATE): dos matrículas simultáneas al mismo curso se ejecutan una tras otra,
     *  así el conteo contra el cupo no se puede pasar por una carrera. */
    @Transactional
    public Matricula matricular(Long cursoId, Long estudianteId) {
        Curso curso = cursoService.buscarPorIdBloqueando(cursoId);

        Usuario estudiante = usuarioRepositorio.buscarPorId(estudianteId)
                .orElseThrow(() -> new ReglaNegocioException("El estudiante indicado no existe"));

        if (estudiante.getRol() != Rol.ESTUDIANTE) {
            throw new ReglaNegocioException("El usuario indicado no tiene rol ESTUDIANTE");
        }

        if (matriculaRepositorio.existePorCursoYEstudiante(cursoId, estudianteId)) {
            throw new ReglaNegocioException("El estudiante ya está matriculado en este curso");
        }

        Integer cupoMaximo = curso.getCupoMaximo();
        if (cupoMaximo != null && matriculaRepositorio.contarPorCurso(cursoId) >= cupoMaximo) {
            throw new ReglaNegocioException("El curso alcanzó su cupo máximo (%s)".formatted(CursoService.estudiantes(cupoMaximo)));
        }

        Matricula matricula = Matricula.builder()
                .cursoId(cursoId)
                .estudianteId(estudianteId)
                .fechaMatricula(LocalDateTime.now())
                .build();

        return matriculaRepositorio.guardar(matricula);
    }

    public List<Matricula> listarPorEstudiante(Long estudianteId, Long usuarioActualId, Rol rolActual) {
        if (rolActual == Rol.ESTUDIANTE && !estudianteId.equals(usuarioActualId)) {
            throw new AccessDeniedException("Solo puedes ver tus propias matrículas");
        }
        return matriculaRepositorio.listarPorEstudiante(estudianteId);
    }

    public List<Matricula> listarPorCurso(Long cursoId, Long usuarioActualId, Rol rolActual) {
        Curso curso = cursoService.buscarPorId(cursoId);

        if (rolActual == Rol.ESTUDIANTE) {
            boolean estaMatriculado = matriculaRepositorio.existePorCursoYEstudiante(cursoId, usuarioActualId);
            if (!estaMatriculado) {
                throw new AccessDeniedException("Solo puedes ver los participantes de un curso en el que estás matriculado");
            }
        } else {
            cursoService.validarPropietario(curso, usuarioActualId, rolActual);
        }

        return matriculaRepositorio.listarPorCurso(cursoId);
    }

    public void eliminar(Long id) {
        matriculaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new MatriculaNoEncontradaException(id));
        matriculaRepositorio.eliminar(id);
    }

    /** Se llama al entrar a la vista del curso; usada para métricas de actividad del estudiante. */
    public void registrarAcceso(Long cursoId, Long estudianteId) {
        if (!matriculaRepositorio.existePorCursoYEstudiante(cursoId, estudianteId)) {
            throw new AccessDeniedException("No tienes permiso para realizar esta acción");
        }
        matriculaRepositorio.registrarAcceso(cursoId, estudianteId, LocalDateTime.now());
    }
}
