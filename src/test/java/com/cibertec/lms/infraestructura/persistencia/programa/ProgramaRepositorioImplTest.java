package com.cibertec.lms.infraestructura.persistencia.programa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.cibertec.lms.dominio.programa.Programa;

class ProgramaRepositorioImplTest {

    @Test
    void buscarPorIdsHaceUnaSolaConsultaYArmaUnMapaPorId() {
        ProgramaJpaRepository jpaRepository = mock(ProgramaJpaRepository.class);
        ProgramaRepositorioImpl repositorio = new ProgramaRepositorioImpl(jpaRepository);

        ProgramaJpaEntity entidad = new ProgramaJpaEntity();
        entidad.setId(9L);
        entidad.setNombre("Ingeniería de Sistemas");
        entidad.setDuracionSemestres(10);
        when(jpaRepository.findAllById(List.of(9L, 99L))).thenReturn(List.of(entidad));

        Map<Long, Programa> resultado = repositorio.buscarPorIds(List.of(9L, 99L));

        assertEquals(1, resultado.size()); // el id 99 no existe y no aparece en el mapa
        assertEquals("Ingeniería de Sistemas", resultado.get(9L).getNombre());
        assertTrue(resultado.get(99L) == null);
    }

    @Test
    void buscarPorIdsConColeccionVaciaNoConsultaLaBase() {
        ProgramaJpaRepository jpaRepository = mock(ProgramaJpaRepository.class);
        ProgramaRepositorioImpl repositorio = new ProgramaRepositorioImpl(jpaRepository);

        Map<Long, Programa> resultado = repositorio.buscarPorIds(List.of());

        assertTrue(resultado.isEmpty());
        verify(jpaRepository, never()).findAllById(anyCollection());
    }
}
