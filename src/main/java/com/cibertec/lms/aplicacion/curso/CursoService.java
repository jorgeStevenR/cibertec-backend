package com.cibertec.lms.aplicacion.curso;

import java.time.LocalDateTime;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.aplicacion.programa.ProgramaService;
import com.cibertec.lms.dominio.comun.Pagina;
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
    private final ProgramaService programaService;

    public Curso crear(String titulo, String descripcion, Long profesorId, Long programaId) {
        Usuario profesor = usuarioRepositorio.buscarPorId(profesorId)
                .orElseThrow(() -> new ReglaNegocioException("El profesor indicado no existe"));

        if (profesor.getRol() != Rol.PROFESOR) {
            throw new ReglaNegocioException("El usuario indicado no tiene rol PROFESOR");
        }

        programaService.buscarPorId(programaId);

        Curso curso = Curso.builder()
                .titulo(titulo)
                .descripcion(descripcion)
                .profesorId(profesorId)
                .programaId(programaId)
                .publicado(false)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return cursoRepositorio.guardar(curso);
    }

    public Curso buscarPorId(Long id) {
        return cursoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new CursoNoEncontradoException(id));
    }

    public Pagina<Curso> listarPaginado(int pagina, int tamano, Long profesorId, Long programaId) {
        return cursoRepositorio.listarPaginado(pagina, tamano, profesorId, programaId);
    }

    public Curso actualizar(
            Long id, String titulo, String descripcion, boolean publicado, Long programaId,
            Long usuarioActualId, Rol rolActual) {
        Curso existente = buscarPorId(id);
        validarPropietario(existente, usuarioActualId, rolActual);

        if (rolActual == Rol.PROFESOR && !programaId.equals(existente.getProgramaId())) {
            throw new AccessDeniedException("Solo un administrador puede cambiar el programa de un curso");
        }
        programaService.buscarPorId(programaId);

        Curso actualizado = Curso.builder()
                .id(existente.getId())
                .titulo(titulo)
                .descripcion(descripcion)
                .profesorId(existente.getProfesorId())
                .programaId(programaId)
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
