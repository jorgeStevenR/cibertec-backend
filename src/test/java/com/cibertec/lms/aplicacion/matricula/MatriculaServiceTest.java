package com.cibertec.lms.aplicacion.matricula;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cibertec.lms.aplicacion.curso.CursoService;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.matricula.Matricula;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

class MatriculaServiceTest {

    private static final Long CURSO = 18L;
    private static final Long ESTUDIANTE = 8L;

    private MatriculaRepositorio matriculas;
    private CursoService cursos;
    private MatriculaService service;

    @BeforeEach
    void preparar() {
        matriculas = mock(MatriculaRepositorio.class);
        cursos = mock(CursoService.class);
        UsuarioRepositorio usuarios = mock(UsuarioRepositorio.class);
        service = new MatriculaService(matriculas, cursos, usuarios);

        when(usuarios.buscarPorId(ESTUDIANTE)).thenReturn(Optional.of(Usuario.builder().id(ESTUDIANTE).rol(Rol.ESTUDIANTE).build()));
        when(matriculas.guardar(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void cursoConCupo(Integer cupo) {
        when(cursos.buscarPorIdBloqueando(CURSO)).thenReturn(Curso.builder().id(CURSO).cupoMaximo(cupo).build());
    }

    @Test
    void cupoLlenoRechazaLaMatriculaYNoGuardaNada() {
        cursoConCupo(2);
        when(matriculas.contarPorCurso(CURSO)).thenReturn(2);

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> service.matricular(CURSO, ESTUDIANTE));

        assertEquals("El curso alcanzó su cupo máximo (2 estudiantes)", error.getMessage());
        verify(matriculas, never()).guardar(any());
    }

    @Test
    void elMensajeUsaSingularConCupoDeUno() {
        cursoConCupo(1);
        when(matriculas.contarPorCurso(CURSO)).thenReturn(1);

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> service.matricular(CURSO, ESTUDIANTE));

        assertEquals("El curso alcanzó su cupo máximo (1 estudiante)", error.getMessage());
    }

    @Test
    void conUnLugarLibreSeMatricula() {
        cursoConCupo(2);
        when(matriculas.contarPorCurso(CURSO)).thenReturn(1);

        Matricula matricula = service.matricular(CURSO, ESTUDIANTE);

        assertEquals(ESTUDIANTE, matricula.getEstudianteId());
        verify(matriculas).guardar(any(Matricula.class));
    }

    @Test
    void sinCupoEsIlimitadoYNoConsultaElConteo() {
        cursoConCupo(null);

        service.matricular(CURSO, ESTUDIANTE);

        verify(matriculas, never()).contarPorCurso(anyLong());
        verify(matriculas).guardar(any(Matricula.class));
    }

    @Test
    void siYaEstabaMatriculadoEseErrorTienePrioridadSobreElCupo() {
        cursoConCupo(1);
        when(matriculas.existePorCursoYEstudiante(CURSO, ESTUDIANTE)).thenReturn(true);
        when(matriculas.contarPorCurso(CURSO)).thenReturn(1);

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> service.matricular(CURSO, ESTUDIANTE));

        assertEquals("El estudiante ya está matriculado en este curso", error.getMessage());
    }

    @Test
    void tomaElBloqueoDelCursoParaSerializarMatriculasConcurrentes() {
        cursoConCupo(null);

        service.matricular(CURSO, ESTUDIANTE);

        verify(cursos).buscarPorIdBloqueando(CURSO);
        verify(cursos, never()).buscarPorId(anyLong());
    }
}
