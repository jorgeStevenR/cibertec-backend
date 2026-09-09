package com.cibertec.lms.aplicacion.curso;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.CursoRepositorio;
import com.cibertec.lms.dominio.excepcion.CursoNoEncontradoException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepositorio cursoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public Curso crear(String titulo, String descripcion, Long profesorId) {
        Usuario profesor = usuarioRepositorio.buscarPorId(profesorId)
                .orElseThrow(() -> new ReglaNegocioException("El profesor indicado no existe"));

        if (profesor.getRol() != Rol.PROFESOR) {
            throw new ReglaNegocioException("El usuario indicado no tiene rol PROFESOR");
        }

        Curso curso = Curso.builder()
                .titulo(titulo)
                .descripcion(descripcion)
                .profesorId(profesorId)
                .publicado(false)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return cursoRepositorio.guardar(curso);
    }

    public Curso buscarPorId(Long id) {
        return cursoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new CursoNoEncontradoException(id));
    }

    public List<Curso> listarTodos() {
        return cursoRepositorio.listarTodos();
    }

    public List<Curso> listarPorProfesor(Long profesorId) {
        return cursoRepositorio.listarPorProfesor(profesorId);
    }

    public Curso actualizar(Long id, String titulo, String descripcion, boolean publicado, Long usuarioActualId, Rol rolActual) {
        Curso existente = buscarPorId(id);
        validarPropietario(existente, usuarioActualId, rolActual);

        Curso actualizado = Curso.builder()
                .id(existente.getId())
                .titulo(titulo)
                .descripcion(descripcion)
                .profesorId(existente.getProfesorId())
                .publicado(publicado)
                .fechaCreacion(existente.getFechaCreacion())
                .build();

        return cursoRepositorio.guardar(actualizado);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        cursoRepositorio.eliminar(id);
    }

    /** El administrador puede administrar cualquier curso; el profesor solo el suyo. */
    public void validarPropietario(Curso curso, Long usuarioActualId, Rol rolActual) {
        if (rolActual == Rol.PROFESOR && !curso.getProfesorId().equals(usuarioActualId)) {
            throw new AccessDeniedException("Solo puedes administrar tus propios cursos");
        }
    }
}
