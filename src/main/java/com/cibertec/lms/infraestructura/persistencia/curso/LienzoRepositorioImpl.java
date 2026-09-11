package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.curso.Lienzo;
import com.cibertec.lms.dominio.curso.LienzoRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class LienzoRepositorioImpl implements LienzoRepositorio {

    private final LienzoJpaRepository jpaRepository;

    @Override
    public Lienzo guardar(Lienzo lienzo) {
        return LienzoMapper.aDominio(jpaRepository.save(LienzoMapper.aEntidad(lienzo)));
    }

    @Override
    public Optional<Lienzo> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(LienzoMapper::aDominio);
    }

    @Override
    public List<Lienzo> listarPorModulo(Long moduloId) {
        return jpaRepository.buscarPorModulo(moduloId).stream().map(LienzoMapper::aDominio).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
