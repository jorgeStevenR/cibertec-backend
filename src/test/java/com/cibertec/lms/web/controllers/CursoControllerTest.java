package com.cibertec.lms.web.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.cibertec.lms.dominio.programa.ProgramaRepositorio;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;
import com.cibertec.lms.web.dtos.CursoResponseDTO;
import com.cibertec.lms.web.dtos.PaginaResponseDTO;

class CursoControllerTest {

    @Test
    void elListadoTraeLosMatriculadosDeTodaLaPaginaConUnaSolaConsultaAgrupada() {
        CursoService service = mock(CursoService.class);
        CursoController controller = new CursoController(service, mock(UsuarioRepositorio.class), mock(ProgramaRepositorio.class));

        Curso conCupo = Curso.builder().id(1L).titulo("A").profesorId(5L).codigo("A-1").periodo("2026-2").cupoMaximo(30)
                .fechaInicio(LocalDate.of(2026, 8, 3)).fechaFin(LocalDate.of(2026, 12, 11)).build();
        Curso sinMatriculas = Curso.builder().id(2L).titulo("B").profesorId(5L).build(); // curso antiguo: campos nuevos en null
        Curso otro = Curso.builder().id(3L).titulo("C").profesorId(5L).build();
        when(service.listarPaginado(0, 20, null, null)).thenReturn(new Pagina<>(List.of(conCupo, sinMatriculas, otro), 0, 20, 3, 1));
        when(service.contarMatriculadosPorCurso(List.of(1L, 2L, 3L))).thenReturn(Map.of(1L, 4, 3L, 2)); // el curso 2 no aparece: 0

        PaginaResponseDTO<CursoResponseDTO> respuesta = controller.listar(null, null, 0, 20);

        assertEquals(List.of(4, 0, 2), respuesta.contenido().stream().map(CursoResponseDTO::matriculados).toList());
        assertEquals("A-1", respuesta.contenido().get(0).codigo());
        assertEquals(30, respuesta.contenido().get(0).cupoMaximo());
        assertEquals(null, respuesta.contenido().get(1).codigo()); // el curso antiguo no rompe el DTO

        verify(service, times(1)).contarMatriculadosPorCurso(anyCollection());
        verify(service, never()).contarMatriculados(anyLong());
    }
}
