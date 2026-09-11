package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.comun.Pagina;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.CursoRepositorio;
import com.cibertec.lms.infraestructura.persistencia.comun.PaginaMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class CursoRepositorioImpl implements CursoRepositorio {

    private final CursoJpaRepository jpaRepository;

    @Override
    public Curso guardar(Curso curso) {
        return CursoMapper.aDominio(jpaRepository.save(CursoMapper.aEntidad(curso)));
    }

    @Override
    public Optional<Curso> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(CursoMapper::aDominio);
    }

    @Override
    public Pagina<Curso> listarPaginado(int pagina, int tamano, Long profesorId, Long programaId) {
        PageRequest pageRequest = PageRequest.of(pagina, tamano);
        var page = (profesorId != null)
                ? jpaRepository.buscarPorProfesorPaginado(profesorId, pageRequest)
                : (programaId != null)
                        ? jpaRepository.buscarPorProgramaPaginado(programaId, pageRequest)
                        : jpaRepository.findAll(pageRequest);
        return PaginaMapper.aDominio(page, CursoMapper::aDominio);
    }

    @Override
    public boolean existePorProgramaId(Long programaId) {
        return jpaRepository.existePorProgramaId(programaId);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
