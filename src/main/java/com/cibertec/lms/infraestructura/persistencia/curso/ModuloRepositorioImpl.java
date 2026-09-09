package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.curso.ModuloRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class ModuloRepositorioImpl implements ModuloRepositorio {

    private final ModuloJpaRepository jpaRepository;

    @Override
    public Modulo guardar(Modulo modulo) {
        return ModuloMapper.aDominio(jpaRepository.save(ModuloMapper.aEntidad(modulo)));
    }

    @Override
    public Optional<Modulo> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ModuloMapper::aDominio);
    }

    @Override
    public List<Modulo> listarPorCurso(Long cursoId) {
        return jpaRepository.buscarPorCursoOrdenado(cursoId).stream().map(ModuloMapper::aDominio).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
