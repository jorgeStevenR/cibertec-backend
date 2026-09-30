package com.cibertec.lms.aplicacion.curso;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.curso.ModuloRepositorio;
import com.cibertec.lms.dominio.excepcion.ModuloNoEncontradoException;
import com.cibertec.lms.dominio.excepcion.SolicitudInvalidaException;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModuloService {

    private final ModuloRepositorio moduloRepositorio;
    private final CursoService cursoService;
    private final PaginaModuloService paginaModuloService;

    public Modulo crear(Long cursoId, String titulo, int orden, Long moduloPadreId, Long usuarioActualId, Rol rolActual) {
        Curso curso = cursoService.buscarPorId(cursoId);
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
        validarPadre(null, moduloPadreId, cursoId);

        Modulo modulo = Modulo.builder()
                .cursoId(cursoId)
                .titulo(titulo)
                .orden(orden)
                .moduloPadreId(moduloPadreId)
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

    public Modulo actualizar(Long id, String titulo, int orden, Long moduloPadreId, Long usuarioActualId, Rol rolActual) {
        Modulo existente = buscarPorId(id);
        Curso curso = cursoService.buscarPorId(existente.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
        validarPadre(id, moduloPadreId, existente.getCursoId());

        Modulo actualizado = Modulo.builder()
                .id(existente.getId())
                .cursoId(existente.getCursoId())
                .titulo(titulo)
                .orden(orden)
                .moduloPadreId(moduloPadreId)
                .build();

        return moduloRepositorio.guardar(actualizado);
    }

    /** moduloId es null cuando se está creando (un módulo nuevo nunca tiene hijos todavía). */
    private void validarPadre(Long moduloId, Long moduloPadreId, Long cursoId) {
        if (moduloPadreId == null) {
            return;
        }
        if (moduloPadreId.equals(moduloId)) {
            throw new SolicitudInvalidaException("Un módulo no puede ser su propio padre");
        }
        Modulo padre = buscarPorId(moduloPadreId);
        if (!padre.getCursoId().equals(cursoId)) {
            throw new SolicitudInvalidaException("El módulo padre debe pertenecer al mismo curso");
        }
        if (padre.getModuloPadreId() != null) {
            throw new SolicitudInvalidaException("Un submódulo no puede tener submódulos (solo un nivel de anidamiento)");
        }
        if (moduloId != null && !moduloRepositorio.listarHijos(moduloId).isEmpty()) {
            throw new SolicitudInvalidaException("Un módulo con submódulos no puede convertirse en submódulo de otro");
        }
    }

    /** moduloPadreId null = reordena los módulos raíz del curso; si no, los hijos de ese módulo. */
    public void reordenar(Long cursoId, Long moduloPadreId, List<Long> idsEnOrden, Long usuarioActualId, Rol rolActual) {
        Curso curso = cursoService.buscarPorId(cursoId);
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);

        List<Modulo> hermanos = moduloRepositorio.listarPorCurso(cursoId).stream()
                .filter(m -> Objects.equals(m.getModuloPadreId(), moduloPadreId))
                .toList();
        Map<Long, Modulo> porId = hermanos.stream().collect(Collectors.toMap(Modulo::getId, m -> m));

        if (idsEnOrden.size() != hermanos.size()
                || new HashSet<>(idsEnOrden).size() != idsEnOrden.size()
                || !porId.keySet().containsAll(idsEnOrden)) {
            throw new SolicitudInvalidaException("La lista de orden no coincide con los módulos de ese grupo");
        }

        for (int i = 0; i < idsEnOrden.size(); i++) {
            Modulo actual = porId.get(idsEnOrden.get(i));
            Modulo actualizado = Modulo.builder()
                    .id(actual.getId())
                    .cursoId(actual.getCursoId())
                    .titulo(actual.getTitulo())
                    .moduloPadreId(actual.getModuloPadreId())
                    .orden(i)
                    .build();
            moduloRepositorio.guardar(actualizado);
        }
    }

    public void eliminar(Long id, Long usuarioActualId, Rol rolActual) {
        Modulo existente = buscarPorId(id);
        Curso curso = cursoService.buscarPorId(existente.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);

        for (Modulo hijo : moduloRepositorio.listarHijos(id)) {
            eliminarContenidoYModulo(hijo.getId());
        }
        eliminarContenidoYModulo(id);
    }

    private void eliminarContenidoYModulo(Long moduloId) {
        paginaModuloService.eliminarPorModulo(moduloId);
        moduloRepositorio.eliminar(moduloId);
    }
}
