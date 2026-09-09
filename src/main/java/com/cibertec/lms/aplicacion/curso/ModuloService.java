package com.cibertec.lms.aplicacion.curso;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.curso.ModuloRepositorio;
import com.cibertec.lms.dominio.excepcion.ModuloNoEncontradoException;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModuloService {

    private final ModuloRepositorio moduloRepositorio;
    private final CursoService cursoService;

    public Modulo crear(Long cursoId, String titulo, int orden, Long usuarioActualId, Rol rolActual) {
        Curso curso = cursoService.buscarPorId(cursoId);
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);

        Modulo modulo = Modulo.builder()
                .cursoId(cursoId)
                .titulo(titulo)
                .orden(orden)
                .build();

        return moduloRepositorio.guardar(modulo);
    }

    public List<Modulo> listarPorCurso(Long cursoId) {
        cursoService.buscarPorId(cursoId);
        return moduloRepositorio.listarPorCurso(cursoId);
    }

    public Modulo buscarPorId(Long id) {
        return moduloRepositorio.buscarPorId(id)
                .orElseThrow(() -> new ModuloNoEncontradoException(id));
    }

    public Modulo actualizar(Long id, String titulo, int orden, Long usuarioActualId, Rol rolActual) {
        Modulo existente = buscarPorId(id);
        Curso curso = cursoService.buscarPorId(existente.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);

        Modulo actualizado = Modulo.builder()
                .id(existente.getId())
                .cursoId(existente.getCursoId())
                .titulo(titulo)
                .orden(orden)
                .build();

        return moduloRepositorio.guardar(actualizado);
    }

    public void eliminar(Long id, Long usuarioActualId, Rol rolActual) {
        Modulo existente = buscarPorId(id);
        Curso curso = cursoService.buscarPorId(existente.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
        moduloRepositorio.eliminar(id);
    }
}
