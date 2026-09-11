package com.cibertec.lms.aplicacion.programa;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.comun.Pagina;
import com.cibertec.lms.dominio.curso.CursoRepositorio;
import com.cibertec.lms.dominio.excepcion.ProgramaNoEncontradoException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.programa.Programa;
import com.cibertec.lms.dominio.programa.ProgramaRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgramaService {

    private final ProgramaRepositorio programaRepositorio;
    private final CursoRepositorio cursoRepositorio;

    public Programa crear(String nombre, String descripcion, int duracionSemestres) {
        Programa programa = Programa.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .duracionSemestres(duracionSemestres)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return programaRepositorio.guardar(programa);
    }

    public Programa buscarPorId(Long id) {
        return programaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new ProgramaNoEncontradoException(id));
    }

    public Pagina<Programa> listarPaginado(int pagina, int tamano, String busqueda) {
        return programaRepositorio.listarPaginado(pagina, tamano, busqueda);
    }

    public Programa actualizar(Long id, String nombre, String descripcion, int duracionSemestres) {
        Programa existente = buscarPorId(id);

        Programa actualizado = Programa.builder()
                .id(existente.getId())
                .nombre(nombre)
                .descripcion(descripcion)
                .duracionSemestres(duracionSemestres)
                .fechaCreacion(existente.getFechaCreacion())
                .build();

        return programaRepositorio.guardar(actualizado);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        if (cursoRepositorio.existePorProgramaId(id)) {
            throw new ReglaNegocioException("No se puede eliminar el programa porque tiene cursos asociados");
        }
        programaRepositorio.eliminar(id);
    }
}
