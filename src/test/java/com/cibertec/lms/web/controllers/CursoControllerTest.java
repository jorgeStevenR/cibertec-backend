package com.cibertec.lms.web.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.cibertec.lms.aplicacion.curso.CursoService;
import com.cibertec.lms.dominio.comun.Pagina;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.programa.Programa;
import com.cibertec.lms.dominio.programa.ProgramaRepositorio;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;
import com.cibertec.lms.web.dtos.CursoResponseDTO;
import com.cibertec.lms.web.dtos.PaginaResponseDTO;

class CursoControllerTest {

    @Test
    void elListadoResuelveMatriculadosProfesoresYProgramasDeTodaLaPaginaConUnaSolaConsultaAgrupadaCadaUno() {
        CursoService service = mock(CursoService.class);
        UsuarioRepositorio usuarioRepositorio = mock(UsuarioRepositorio.class);
        ProgramaRepositorio programaRepositorio = mock(ProgramaRepositorio.class);
        CursoController controller = new CursoController(service, usuarioRepositorio, programaRepositorio);

        Curso conCupo = Curso.builder().id(1L).titulo("A").profesorId(5L).programaId(9L).codigo("A-1").periodo("2026-2")
                .cupoMaximo(30).fechaInicio(LocalDate.of(2026, 8, 3)).fechaFin(LocalDate.of(2026, 12, 11)).build();
        Curso sinMatriculas = Curso.builder().id(2L).titulo("B").profesorId(5L).build(); // curso antiguo: campos nuevos en null
        Curso otro = Curso.builder().id(3L).titulo("C").profesorId(6L).programaId(9L).build();
        when(service.listarPaginado(0, 20, null, null)).thenReturn(new Pagina<>(List.of(conCupo, sinMatriculas, otro), 0, 20, 3, 1));
        when(service.contarMatriculadosPorCurso(List.of(1L, 2L, 3L))).thenReturn(Map.of(1L, 4, 3L, 2)); // el curso 2 no aparece: 0
        when(usuarioRepositorio.buscarPorIds(any())).thenReturn(Map.of(
                5L, Usuario.builder().id(5L).nombre("Marco").apellido("Aguilar").email("marco@cibertec.test").build()));
        when(programaRepositorio.buscarPorIds(any())).thenReturn(Map.of(
                9L, Programa.builder().id(9L).nombre("Ingeniería de Sistemas").build()));

        PaginaResponseDTO<CursoResponseDTO> respuesta = controller.listar(null, null, 0, 20);

        assertEquals(List.of(4, 0, 2), respuesta.contenido().stream().map(CursoResponseDTO::matriculados).toList());
        assertEquals("A-1", respuesta.contenido().get(0).codigo());
        assertEquals(30, respuesta.contenido().get(0).cupoMaximo());
        assertEquals(null, respuesta.contenido().get(1).codigo()); // el curso antiguo no rompe el DTO
        assertEquals("Marco Aguilar", respuesta.contenido().get(0).profesorNombre());
        assertEquals(null, respuesta.contenido().get(2).profesorNombre()); // profesor 6 no vino en el mapa -> null, no rompe
        assertEquals("Ingeniería de Sistemas", respuesta.contenido().get(0).programaNombre());
        assertEquals(null, respuesta.contenido().get(1).programaNombre()); // sin programaId -> null, no se busca

        verify(service, times(1)).contarMatriculadosPorCurso(anyCollection());
        verify(service, never()).contarMatriculados(anyLong());
        verify(usuarioRepositorio, times(1)).buscarPorIds(anyCollection());
        verify(usuarioRepositorio, never()).buscarPorId(anyLong());
        verify(programaRepositorio, times(1)).buscarPorIds(anyCollection());
        verify(programaRepositorio, never()).buscarPorId(anyLong());
    }
}
