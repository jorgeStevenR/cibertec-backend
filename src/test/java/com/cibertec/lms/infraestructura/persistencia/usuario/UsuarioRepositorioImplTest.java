package com.cibertec.lms.infraestructura.persistencia.usuario;

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

import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.Usuario;

class UsuarioRepositorioImplTest {

    @Test
    void buscarPorIdsHaceUnaSolaConsultaYArmaUnMapaPorId() {
        UsuarioJpaRepository jpaRepository = mock(UsuarioJpaRepository.class);
        UsuarioRepositorioImpl repositorio = new UsuarioRepositorioImpl(jpaRepository);

        UsuarioJpaEntity entidad1 = entidad(1L, "Camila", "Torres");
        UsuarioJpaEntity entidad2 = entidad(2L, "Andrés", "Paredes");
        when(jpaRepository.findAllById(List.of(1L, 2L, 3L))).thenReturn(List.of(entidad1, entidad2));

        Map<Long, Usuario> resultado = repositorio.buscarPorIds(List.of(1L, 2L, 3L));

        assertEquals(2, resultado.size()); // el id 3 no existe y no aparece en el mapa
        assertEquals("Camila", resultado.get(1L).getNombre());
        assertEquals("Andrés", resultado.get(2L).getNombre());
        assertTrue(resultado.get(3L) == null);
    }

    @Test
    void buscarPorIdsConColeccionVaciaNoConsultaLaBase() {
        UsuarioJpaRepository jpaRepository = mock(UsuarioJpaRepository.class);
        UsuarioRepositorioImpl repositorio = new UsuarioRepositorioImpl(jpaRepository);

        Map<Long, Usuario> resultado = repositorio.buscarPorIds(List.of());

        assertTrue(resultado.isEmpty());
        verify(jpaRepository, never()).findAllById(anyCollection());
    }

    private static UsuarioJpaEntity entidad(Long id, String nombre, String apellido) {
        UsuarioJpaEntity entidad = new UsuarioJpaEntity();
        entidad.setId(id);
        entidad.setNombre(nombre);
        entidad.setApellido(apellido);
        entidad.setEmail(nombre.toLowerCase() + "@cibertec.test");
        entidad.setPasswordHash("hash");
        entidad.setRol(Rol.ESTUDIANTE);
        entidad.setActivo(true);
        return entidad;
    }
}
