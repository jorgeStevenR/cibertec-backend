package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.CursoRepositorio;

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
    public List<Curso> listarTodos() {
        return jpaRepository.findAll().stream().map(CursoMapper::aDominio).toList();
    }

    @Override
    public List<Curso> listarPorProfesor(Long profesorId) {
        return jpaRepository.buscarPorProfesor(profesorId).stream().map(CursoMapper::aDominio).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
