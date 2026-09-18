package com.cibertec.lms.aplicacion.estudiante;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.estudiante.ActividadDeEstudiante;
import com.cibertec.lms.dominio.estudiante.AvanceEstudianteRepositorio;
import com.cibertec.lms.dominio.estudiante.CalificacionesCurso;
import com.cibertec.lms.dominio.estudiante.CuestionarioDeEstudiante;
import com.cibertec.lms.dominio.estudiante.CursoDeEstudiante;
import com.cibertec.lms.dominio.estudiante.EstadoCalificacion;
import com.cibertec.lms.dominio.estudiante.EstadoVencimiento;
import com.cibertec.lms.dominio.estudiante.ItemCalificacion;
import com.cibertec.lms.dominio.estudiante.ResumenCurso;
import com.cibertec.lms.dominio.estudiante.TipoItem;
import com.cibertec.lms.dominio.estudiante.Vencimiento;
import com.cibertec.lms.dominio.excepcion.SolicitudInvalidaException;
import com.cibertec.lms.dominio.excepcion.UsuarioNoEncontradoException;
import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;

/**
 * Vistas agregadas de solo lectura para el panel del estudiante. Cada operación usa un número fijo de consultas
 * (cursos + actividades + cuestionarios) sin importar cuántos cursos o ítems tenga el estudiante.
 *
 * Solo cuentan cursos PUBLICADOS en los que el estudiante está matriculado. Los cuestionarios cuentan todos (estén
 * abiertos o no todavía): así el total de un curso no cambia según la fecha en que se consulta.
 */
@Service
@RequiredArgsConstructor
public class AvanceEstudianteService {

    private static final int DIAS_VENTANA_POR_DEFECTO = 60;

    private final AvanceEstudianteRepositorio avanceRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public List<ResumenCurso> resumenPorCurso(Long estudianteId, Long usuarioActualId, Rol rolActual) {
        validarAcceso(estudianteId, usuarioActualId, rolActual);

        List<CursoDeEstudiante> cursos = avanceRepositorio.listarCursosPublicadosMatriculados(estudianteId);
        List<Long> cursoIds = idsDe(cursos);
        Map<Long, List<ActividadDeEstudiante>> actividades = avanceRepositorio.listarActividades(estudianteId, cursoIds)
                .stream().collect(Collectors.groupingBy(ActividadDeEstudiante::cursoId));
        Map<Long, List<CuestionarioDeEstudiante>> cuestionarios = avanceRepositorio.listarCuestionarios(estudianteId, cursoIds)
                .stream().collect(Collectors.groupingBy(CuestionarioDeEstudiante::cursoId));

        return cursos.stream()
                .map(curso -> armarResumen(
                        curso,
                        actividades.getOrDefault(curso.cursoId(), List.of()),
                        cuestionarios.getOrDefault(curso.cursoId(), List.of())))
                .toList();
    }

    /** Sin desde: inicio del mes actual. Sin hasta: 60 días después de desde. El rango es inclusivo en ambos extremos (días completos). */
    public List<Vencimiento> vencimientos(
            Long estudianteId, LocalDate desde, LocalDate hasta, Long usuarioActualId, Rol rolActual) {
        validarAcceso(estudianteId, usuarioActualId, rolActual);

        LocalDate desdeEfectivo = desde != null ? desde : LocalDate.now().withDayOfMonth(1);
        LocalDate hastaEfectivo = hasta != null ? hasta : desdeEfectivo.plusDays(DIAS_VENTANA_POR_DEFECTO);
        if (desdeEfectivo.isAfter(hastaEfectivo)) {
            throw new SolicitudInvalidaException("El parámetro 'desde' no puede ser posterior a 'hasta'");
        }
        LocalDateTime inicio = desdeEfectivo.atStartOfDay();
        LocalDateTime finExclusivo = hastaEfectivo.plusDays(1).atStartOfDay();
        LocalDateTime ahora = LocalDateTime.now();

        List<CursoDeEstudiante> cursos = avanceRepositorio.listarCursosPublicadosMatriculados(estudianteId);
        List<Long> cursoIds = idsDe(cursos);
        Map<Long, String> tituloPorCurso = cursos.stream()
                .collect(Collectors.toMap(CursoDeEstudiante::cursoId, CursoDeEstudiante::titulo));

        List<Vencimiento> resultado = new ArrayList<>();
        for (ActividadDeEstudiante a : avanceRepositorio.listarActividades(estudianteId, cursoIds)) {
            if (enRango(a.fechaLimite(), inicio, finExclusivo)) {
                EstadoVencimiento estado = a.entregada() ? EstadoVencimiento.ENTREGADA : estadoPendienteOVencida(a.fechaLimite(), ahora);
                resultado.add(new Vencimiento(TipoItem.ACTIVIDAD, a.id(), a.titulo(), a.cursoId(),
                        tituloPorCurso.get(a.cursoId()), a.moduloId(), a.fechaLimite(), estado));
            }
        }
        for (CuestionarioDeEstudiante q : avanceRepositorio.listarCuestionarios(estudianteId, cursoIds)) {
            if (enRango(q.fechaLimite(), inicio, finExclusivo)) {
                EstadoVencimiento estado = q.rendido() ? EstadoVencimiento.RENDIDO : estadoPendienteOVencida(q.fechaLimite(), ahora);
                resultado.add(new Vencimiento(TipoItem.CUESTIONARIO, q.id(), q.titulo(), q.cursoId(),
                        tituloPorCurso.get(q.cursoId()), q.moduloId(), q.fechaLimite(), estado));
            }
        }

        resultado.sort(Comparator.comparing(Vencimiento::fechaLimite)
                .thenComparing(Vencimiento::tipo)
                .thenComparing(Vencimiento::id));
        return resultado;
    }

    /** Con cursoId: solo ese curso, y solo si el estudiante está matriculado en él y está publicado (si no, lista vacía). */
    public List<CalificacionesCurso> calificaciones(
            Long estudianteId, Long cursoId, Long usuarioActualId, Rol rolActual) {
        validarAcceso(estudianteId, usuarioActualId, rolActual);

        List<CursoDeEstudiante> cursos = avanceRepositorio.listarCursosPublicadosMatriculados(estudianteId).stream()
                .filter(curso -> cursoId == null || curso.cursoId().equals(cursoId))
                .toList();
        List<Long> cursoIds = idsDe(cursos);
        Map<Long, List<ActividadDeEstudiante>> actividades = avanceRepositorio.listarActividades(estudianteId, cursoIds)
                .stream().collect(Collectors.groupingBy(ActividadDeEstudiante::cursoId));
        Map<Long, List<CuestionarioDeEstudiante>> cuestionarios = avanceRepositorio.listarCuestionarios(estudianteId, cursoIds)
                .stream().collect(Collectors.groupingBy(CuestionarioDeEstudiante::cursoId));

        return cursos.stream()
                .map(curso -> armarCalificaciones(
                        curso,
                        actividades.getOrDefault(curso.cursoId(), List.of()),
                        cuestionarios.getOrDefault(curso.cursoId(), List.of())))
                .toList();
    }

    private ResumenCurso armarResumen(CursoDeEstudiante curso, List<ActividadDeEstudiante> actividades, List<CuestionarioDeEstudiante> cuestionarios) {
        int totalActividades = actividades.size();
        int entregadas = (int) actividades.stream().filter(ActividadDeEstudiante::entregada).count();
        int totalCuestionarios = cuestionarios.size();
        int rendidos = (int) cuestionarios.stream().filter(CuestionarioDeEstudiante::rendido).count();

        int total = totalActividades + totalCuestionarios;
        int porcentaje = total == 0 ? 0 : (int) Math.round(100.0 * (entregadas + rendidos) / total);

        return new ResumenCurso(curso.cursoId(), totalActividades, entregadas, totalActividades - entregadas,
                totalCuestionarios, rendidos, totalCuestionarios - rendidos, porcentaje, curso.fechaInicio(), curso.fechaFin());
    }

    private CalificacionesCurso armarCalificaciones(
            CursoDeEstudiante curso, List<ActividadDeEstudiante> actividades, List<CuestionarioDeEstudiante> cuestionarios) {
        List<FilaOrdenable> filas = new ArrayList<>();
        actividades.forEach(a -> filas.add(new FilaOrdenable(a.moduloOrden(), a.fechaCreacion(), aItem(a))));
        cuestionarios.forEach(q -> filas.add(new FilaOrdenable(q.moduloOrden(), q.fechaCreacion(), aItem(q))));
        filas.sort(Comparator.comparingInt(FilaOrdenable::moduloOrden).thenComparing(FilaOrdenable::fechaCreacion));
        List<ItemCalificacion> items = filas.stream().map(FilaOrdenable::item).toList();

        double obtenido = items.stream().filter(ItemCalificacion::calificado).mapToDouble(ItemCalificacion::nota).sum();
        double maximoCalificado = items.stream().filter(ItemCalificacion::calificado).mapToDouble(ItemCalificacion::puntajeMaximo).sum();

        return new CalificacionesCurso(curso.cursoId(), curso.titulo(), redondear(obtenido), redondear(maximoCalificado), items,
                curso.fechaInicio(), curso.fechaFin());
    }

    private ItemCalificacion aItem(ActividadDeEstudiante a) {
        EstadoCalificacion estado = !a.entregada() ? EstadoCalificacion.SIN_ENTREGAR
                : a.nota() == null ? EstadoCalificacion.ENTREGADA : EstadoCalificacion.CALIFICADA;
        return new ItemCalificacion(TipoItem.ACTIVIDAD, a.id(), a.titulo(), a.moduloId(), a.moduloTitulo(),
                a.puntajeMaximo(), a.nota(), estado, a.fechaEntrega(), a.fechaCalificacion(), a.comentarioDocente());
    }

    /** Un cuestionario rendido ya está calificado (se autocalifica al enviarlo), así que su fecha de calificación es la de la rendición.
     *  El puntaje máximo de un rendido es el que quedó guardado en el intento, para que coincida con la escala de su nota. */
    private ItemCalificacion aItem(CuestionarioDeEstudiante q) {
        if (!q.rendido()) {
            return new ItemCalificacion(TipoItem.CUESTIONARIO, q.id(), q.titulo(), q.moduloId(), q.moduloTitulo(),
                    q.puntajeMaximoActual(), null, EstadoCalificacion.SIN_ENTREGAR, null, null, null);
        }
        return new ItemCalificacion(TipoItem.CUESTIONARIO, q.id(), q.titulo(), q.moduloId(), q.moduloTitulo(),
                q.puntajeMaximoDelIntento(), q.nota(), EstadoCalificacion.RENDIDO, q.fechaRendicion(), q.fechaRendicion(), null);
    }

    /** ESTUDIANTE solo se consulta a sí mismo; ADMINISTRADOR (único otro rol que llega acá por @PreAuthorize) puede consultar
     *  a cualquiera, pero el estudiante tiene que existir. */
    private void validarAcceso(Long estudianteId, Long usuarioActualId, Rol rolActual) {
        if (rolActual == Rol.ESTUDIANTE) {
            if (!estudianteId.equals(usuarioActualId)) {
                throw new AccessDeniedException("Solo puedes consultar tu propio avance");
            }
            return;
        }
        usuarioRepositorio.buscarPorId(estudianteId).orElseThrow(() -> new UsuarioNoEncontradoException(estudianteId));
    }

    private static List<Long> idsDe(List<CursoDeEstudiante> cursos) {
        return cursos.stream().map(CursoDeEstudiante::cursoId).toList();
    }

    private static boolean enRango(LocalDateTime fecha, LocalDateTime inicio, LocalDateTime finExclusivo) {
        return fecha != null && !fecha.isBefore(inicio) && fecha.isBefore(finExclusivo);
    }

    private static EstadoVencimiento estadoPendienteOVencida(LocalDateTime fechaLimite, LocalDateTime ahora) {
        return fechaLimite.isBefore(ahora) ? EstadoVencimiento.VENCIDA : EstadoVencimiento.PENDIENTE;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private record FilaOrdenable(int moduloOrden, LocalDateTime fechaCreacion, ItemCalificacion item) {
    }
}
