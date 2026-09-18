package com.cibertec.lms.aplicacion.curso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import com.cibertec.lms.aplicacion.programa.ProgramaService;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.CursoRepositorio;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.excepcion.SolicitudInvalidaException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

class CursoServiceTest {

    private static final Long PROFESOR = 5L;
    private static final Long ADMIN = 1L;
    private static final Long CURSO = 18L;

    private CursoRepositorio cursos;
    private UsuarioRepositorio usuarios;
    private MatriculaRepositorio matriculas;
    private CursoService service;

    @BeforeEach
    void preparar() {
        cursos = mock(CursoRepositorio.class);
        usuarios = mock(UsuarioRepositorio.class);
        matriculas = mock(MatriculaRepositorio.class);
        service = new CursoService(cursos, usuarios, mock(ProgramaService.class), matriculas);

        when(usuarios.buscarPorId(PROFESOR)).thenReturn(Optional.of(Usuario.builder().id(PROFESOR).rol(Rol.PROFESOR).build()));
        when(cursos.guardar(any(Curso.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cursos.buscarPorIdBloqueando(CURSO)).thenReturn(Optional.of(existente()));
    }

    private static Curso existente() {
        return Curso.builder()
                .id(CURSO).titulo("Desarrollo Web").descripcion("desc").profesorId(PROFESOR).programaId(2L).publicado(true)
                .fechaCreacion(LocalDateTime.of(2026, 9, 1, 10, 0))
                .codigo("WFS-2026-B").periodo("2026-2").cupoMaximo(30)
                .fechaInicio(LocalDate.of(2026, 8, 3)).fechaFin(LocalDate.of(2026, 12, 11))
                .build();
    }

    private static CrearCursoComando crear(String codigo, LocalDate inicio, LocalDate fin) {
        return new CrearCursoComando("Desarrollo Web", "desc", PROFESOR, 2L, codigo, " 2026-2 ", 30, inicio, fin);
    }

    private static ActualizarCursoComando actualizar(String codigo, String periodo, Integer cupo, LocalDate inicio, LocalDate fin) {
        return new ActualizarCursoComando("Nuevo título", "nueva", true, 2L, codigo, periodo, cupo, inicio, fin);
    }

    @Test
    void crearNormalizaCodigoYPeriodoYNacenSinPublicar() {
        when(cursos.existePorCodigo("WFS-2026-A")).thenReturn(false);

        Curso creado = service.crear(crear("  wfs-2026-a ", LocalDate.of(2026, 8, 3), LocalDate.of(2026, 12, 11)));

        ArgumentCaptor<Curso> guardado = ArgumentCaptor.forClass(Curso.class);
        verify(cursos).guardar(guardado.capture());
        assertEquals("WFS-2026-A", guardado.getValue().getCodigo());
        assertEquals("2026-2", creado.getPeriodo());
        assertEquals(30, creado.getCupoMaximo());
        assertFalse(creado.isPublicado());
    }

    @Test
    void crearRechazaCodigoDuplicadoConMensajeLegible() {
        when(cursos.existePorCodigo("WFS-2026-A")).thenReturn(true);

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> service.crear(crear("wfs-2026-a", LocalDate.of(2026, 8, 3), LocalDate.of(2026, 12, 11))));

        assertEquals("Ya existe un curso con el código WFS-2026-A", error.getMessage());
        verify(cursos, never()).guardar(any());
    }

    @Test
    void crearRechazaFechaFinAnteriorAInicio() {
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> service.crear(crear("WFS-2026-A", LocalDate.of(2026, 12, 11), LocalDate.of(2026, 8, 3))));

        assertEquals("La fecha de fin no puede ser anterior a la de inicio", error.getMessage());
        verify(cursos, never()).guardar(any());
    }

    @Test
    void crearAceptaInicioIgualAFin() {
        service.crear(crear("WFS-2026-A", LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 3)));
        verify(cursos).guardar(any(Curso.class));
    }

    @Test
    void adminActualizaTodoYReemplazaLosDatosAdministrativos() {
        Curso actualizado = service.actualizar(CURSO,
                actualizar("bdr-2026-b", "2026-1", null, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 6, 30)), ADMIN, Rol.ADMINISTRADOR);

        assertEquals("BDR-2026-B", actualizado.getCodigo());
        assertEquals("2026-1", actualizado.getPeriodo());
        assertNull(actualizado.getCupoMaximo()); // null = sin límite
        assertEquals(LocalDate.of(2026, 2, 1), actualizado.getFechaInicio());
    }

    @Test
    void adminPuedeConservarSuPropioCodigoPeroNoUsarElDeOtroCurso() {
        when(cursos.buscarPorCodigo("WFS-2026-B")).thenReturn(Optional.of(existente()));
        service.actualizar(CURSO, actualizar("wfs-2026-b", "2026-2", 30, LocalDate.of(2026, 8, 3), LocalDate.of(2026, 12, 11)), ADMIN, Rol.ADMINISTRADOR);

        Curso otro = Curso.builder().id(99L).codigo("OTRO-1").build();
        when(cursos.buscarPorCodigo("OTRO-1")).thenReturn(Optional.of(otro));
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> service.actualizar(CURSO,
                actualizar("otro-1", "2026-2", 30, LocalDate.of(2026, 8, 3), LocalDate.of(2026, 12, 11)), ADMIN, Rol.ADMINISTRADOR));
        assertEquals("Ya existe un curso con el código OTRO-1", error.getMessage());
    }

    @Test
    void adminDebeEnviarLosDatosAdministrativos() {
        assertThrows(SolicitudInvalidaException.class, () -> service.actualizar(CURSO,
                actualizar(null, "2026-2", 30, LocalDate.of(2026, 8, 3), LocalDate.of(2026, 12, 11)), ADMIN, Rol.ADMINISTRADOR));
        assertThrows(SolicitudInvalidaException.class, () -> service.actualizar(CURSO,
                actualizar("X", "2026-2", 30, null, LocalDate.of(2026, 12, 11)), ADMIN, Rol.ADMINISTRADOR));
        verify(cursos, never()).guardar(any());
    }

    @Test
    void adminNoPuedeBajarElCupoPorDebajoDeLosMatriculados() {
        when(matriculas.contarPorCurso(CURSO)).thenReturn(5);

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> service.actualizar(CURSO,
                actualizar("WFS-2026-B", "2026-2", 3, LocalDate.of(2026, 8, 3), LocalDate.of(2026, 12, 11)), ADMIN, Rol.ADMINISTRADOR));

        assertTrue(error.getMessage().contains("3") && error.getMessage().contains("5"), error.getMessage());
        verify(cursos, never()).guardar(any());

        // igual al número de matriculados sí se puede
        Curso ok = service.actualizar(CURSO,
                actualizar("WFS-2026-B", "2026-2", 5, LocalDate.of(2026, 8, 3), LocalDate.of(2026, 12, 11)), ADMIN, Rol.ADMINISTRADOR);
        assertEquals(5, ok.getCupoMaximo());
    }

    @Test
    void profesorNoPuedeCambiarDatosAdministrativos() {
        assertThrows(AccessDeniedException.class, () -> service.actualizar(CURSO,
                actualizar("OTRO-CODIGO", null, null, null, null), PROFESOR, Rol.PROFESOR));
        assertThrows(AccessDeniedException.class, () -> service.actualizar(CURSO,
                actualizar(null, null, 99, null, null), PROFESOR, Rol.PROFESOR));
        assertThrows(AccessDeniedException.class, () -> service.actualizar(CURSO,
                actualizar(null, null, null, null, LocalDate.of(2027, 1, 1)), PROFESOR, Rol.PROFESOR));
        verify(cursos, never()).guardar(any());
    }

    @Test
    void profesorEditaTituloYConservaLosDatosAdministrativosSiLosOmiteOLosEnviaIguales() {
        Curso omitidos = service.actualizar(CURSO, actualizar(null, null, null, null, null), PROFESOR, Rol.PROFESOR);
        assertEquals("Nuevo título", omitidos.getTitulo());
        assertEquals("WFS-2026-B", omitidos.getCodigo());
        assertEquals(30, omitidos.getCupoMaximo());

        Curso iguales = service.actualizar(CURSO,
                actualizar("wfs-2026-b", "2026-2", 30, LocalDate.of(2026, 8, 3), LocalDate.of(2026, 12, 11)), PROFESOR, Rol.PROFESOR);
        assertEquals("WFS-2026-B", iguales.getCodigo());
        assertEquals(LocalDate.of(2026, 12, 11), iguales.getFechaFin());
    }

    @Test
    void profesorAjenoNoPuedeActualizar() {
        assertThrows(AccessDeniedException.class, () -> service.actualizar(CURSO,
                actualizar(null, null, null, null, null), 77L, Rol.PROFESOR));
    }

    @Test
    void actualizarConservaLosDatosDeUnCursoAntiguoConCamposNulosParaElProfesor() {
        Curso antiguo = Curso.builder().id(CURSO).titulo("Viejo").profesorId(PROFESOR).programaId(2L).build();
        when(cursos.buscarPorIdBloqueando(CURSO)).thenReturn(Optional.of(antiguo));

        Curso actualizado = service.actualizar(CURSO, actualizar(null, null, null, null, null), PROFESOR, Rol.PROFESOR);

        assertNull(actualizado.getCodigo());
        assertNull(actualizado.getFechaInicio());
    }
}
