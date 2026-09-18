package com.cibertec.lms.aplicacion.estudiante;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

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
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

class AvanceEstudianteServiceTest {

    private static final Long ESTUDIANTE = 8L;
    private static final Long CURSO = 11L;

    private AvanceEstudianteRepositorio avance;
    private UsuarioRepositorio usuarios;
    private AvanceEstudianteService service;

    @BeforeEach
    void preparar() {
        avance = mock(AvanceEstudianteRepositorio.class);
        usuarios = mock(UsuarioRepositorio.class);
        service = new AvanceEstudianteService(avance, usuarios);
        when(avance.listarCursosPublicadosMatriculados(ESTUDIANTE)).thenReturn(List.of(new CursoDeEstudiante(CURSO, "Bases de Datos", LocalDate.of(2026, 8, 3), LocalDate.of(2026, 12, 11))));
        when(avance.listarActividades(anyLong(), anyCollection())).thenReturn(List.of());
        when(avance.listarCuestionarios(anyLong(), anyCollection())).thenReturn(List.of());
    }

    private static ActividadDeEstudiante actividad(long id, LocalDateTime limite, LocalDateTime entrega, Double nota) {
        return new ActividadDeEstudiante(id, "A" + id, limite, 20.0, LocalDateTime.of(2026, 1, 1, 0, 0).plusMinutes(id),
                7L, "Modulo 1", 1, CURSO, entrega, nota, nota != null ? entrega : null, nota != null ? "bien" : null);
    }

    private static CuestionarioDeEstudiante cuestionario(long id, LocalDateTime limite, LocalDateTime rendicion, Double nota, Double maxIntento) {
        return new CuestionarioDeEstudiante(id, "Q" + id, limite, 12.0, LocalDateTime.of(2026, 1, 1, 0, 0).plusMinutes(id),
                7L, "Modulo 1", 1, CURSO, rendicion, nota, maxIntento);
    }

    @Test
    void porcentajeSeRedondeaYSinItemsEsCero() {
        when(avance.listarActividades(ESTUDIANTE, List.of(CURSO))).thenReturn(List.of(
                actividad(1, null, LocalDateTime.now(), null)));
        when(avance.listarCuestionarios(ESTUDIANTE, List.of(CURSO))).thenReturn(List.of(
                cuestionario(1, null, LocalDateTime.now(), 5.0, 10.0),
                cuestionario(2, null, null, null, null)));

        ResumenCurso resumen = service.resumenPorCurso(ESTUDIANTE, ESTUDIANTE, Rol.ESTUDIANTE).get(0);

        assertEquals(1, resumen.totalActividades());
        assertEquals(1, resumen.actividadesEntregadas());
        assertEquals(0, resumen.actividadesPendientes());
        assertEquals(2, resumen.totalCuestionarios());
        assertEquals(1, resumen.cuestionariosRendidos());
        assertEquals(1, resumen.cuestionariosPendientes());
        assertEquals(67, resumen.porcentajeCompletado()); // 2 de 3 = 66.67

        when(avance.listarActividades(ESTUDIANTE, List.of(CURSO))).thenReturn(List.of());
        when(avance.listarCuestionarios(ESTUDIANTE, List.of(CURSO))).thenReturn(List.of());
        assertEquals(0, service.resumenPorCurso(ESTUDIANTE, ESTUDIANTE, Rol.ESTUDIANTE).get(0).porcentajeCompletado());
    }

    @Test
    void estudianteNoPuedeConsultarAOtroYNoTocaLaBase() {
        assertThrows(AccessDeniedException.class, () -> service.resumenPorCurso(ESTUDIANTE, 99L, Rol.ESTUDIANTE));
        assertThrows(AccessDeniedException.class, () -> service.calificaciones(ESTUDIANTE, null, 99L, Rol.ESTUDIANTE));
        verifyNoInteractions(avance);
    }

    @Test
    void administradorConsultaAEstudianteExistenteYFallaSiNoExiste() {
        when(usuarios.buscarPorId(ESTUDIANTE)).thenReturn(Optional.of(mock(Usuario.class)));
        when(usuarios.buscarPorId(404L)).thenReturn(Optional.empty());

        assertEquals(1, service.resumenPorCurso(ESTUDIANTE, 1L, Rol.ADMINISTRADOR).size());
        assertThrows(UsuarioNoEncontradoException.class, () -> service.resumenPorCurso(404L, 1L, Rol.ADMINISTRADOR));
    }

    @Test
    void vencimientosClasificaEstadosOrdenaYFiltraPorRango() {
        LocalDateTime ahora = LocalDateTime.now();
        when(avance.listarActividades(ESTUDIANTE, List.of(CURSO))).thenReturn(List.of(
                actividad(1, ahora.plusDays(5), null, null),              // PENDIENTE
                actividad(2, ahora.minusDays(2), null, null),             // VENCIDA
                actividad(3, ahora.plusDays(1), ahora, null),             // ENTREGADA
                actividad(4, null, null, null),                           // sin fecha: no aparece
                actividad(5, ahora.plusDays(400), null, null)));          // fuera de rango
        when(avance.listarCuestionarios(ESTUDIANTE, List.of(CURSO))).thenReturn(List.of(
                cuestionario(1, ahora.plusDays(3), ahora, 8.0, 12.0)));   // RENDIDO

        List<Vencimiento> resultado = service.vencimientos(
                ESTUDIANTE, LocalDate.now().minusDays(10), LocalDate.now().plusDays(30), ESTUDIANTE, Rol.ESTUDIANTE);

        assertEquals(4, resultado.size()); // entran 3 actividades y 1 cuestionario; sin fecha y fuera de rango quedan afuera
        assertEquals(List.of(EstadoVencimiento.VENCIDA, EstadoVencimiento.ENTREGADA, EstadoVencimiento.RENDIDO, EstadoVencimiento.PENDIENTE),
                resultado.stream().map(Vencimiento::estado).toList());
        assertEquals(TipoItem.CUESTIONARIO, resultado.get(2).tipo());
        assertEquals("Bases de Datos", resultado.get(0).cursoTitulo());
    }

    @Test
    void vencimientosRechazaRangoInvertido() {
        assertThrows(SolicitudInvalidaException.class, () -> service.vencimientos(
                ESTUDIANTE, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 9, 1), ESTUDIANTE, Rol.ESTUDIANTE));
    }

    @Test
    void calificacionesSumaSoloLoCalificadoYUsaElMaximoDelIntento() {
        LocalDateTime rendicion = LocalDateTime.of(2026, 9, 17, 9, 0);
        when(avance.listarActividades(ESTUDIANTE, List.of(CURSO))).thenReturn(List.of(
                actividad(1, null, LocalDateTime.now(), 15.0),  // CALIFICADA
                actividad(2, null, LocalDateTime.now(), null),  // ENTREGADA sin nota
                actividad(3, null, null, null)));               // SIN_ENTREGAR
        when(avance.listarCuestionarios(ESTUDIANTE, List.of(CURSO))).thenReturn(List.of(
                cuestionario(1, null, rendicion, 9.0, 10.0),    // RENDIDO, maximo del intento = 10 (no 12)
                cuestionario(2, null, null, null, null)));      // SIN_ENTREGAR, maximo actual = 12

        CalificacionesCurso curso = service.calificaciones(ESTUDIANTE, null, ESTUDIANTE, Rol.ESTUDIANTE).get(0);

        assertEquals(24.0, curso.puntajeObtenido());          // 15 + 9
        assertEquals(30.0, curso.puntajeMaximoCalificado());  // 20 + 10
        assertEquals(5, curso.items().size());

        ItemCalificacion rendido = curso.items().stream()
                .filter(i -> i.tipo() == TipoItem.CUESTIONARIO && i.estado() == EstadoCalificacion.RENDIDO).findFirst().orElseThrow();
        assertEquals(10.0, rendido.puntajeMaximo());
        assertEquals(rendicion, rendido.fechaCalificacion());
        assertNull(rendido.comentarioDocente());

        assertEquals(List.of(EstadoCalificacion.CALIFICADA, EstadoCalificacion.ENTREGADA, EstadoCalificacion.SIN_ENTREGAR),
                curso.items().stream().filter(i -> i.tipo() == TipoItem.ACTIVIDAD).map(ItemCalificacion::estado).toList());
    }

    @Test
    void calificacionesConCursoNoMatriculadoDevuelveVacio() {
        assertEquals(List.of(), service.calificaciones(ESTUDIANTE, 999L, ESTUDIANTE, Rol.ESTUDIANTE));
    }
}
