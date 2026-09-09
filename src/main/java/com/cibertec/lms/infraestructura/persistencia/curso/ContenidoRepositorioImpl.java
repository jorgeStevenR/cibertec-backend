package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.curso.ContenidoModulo;
import com.cibertec.lms.dominio.curso.ContenidoRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class ContenidoRepositorioImpl implements ContenidoRepositorio {

    private final ContenidoJpaRepository jpaRepository;

    @Override
    public ContenidoModulo guardar(ContenidoModulo contenido) {
        return ContenidoMapper.aDominio(jpaRepository.save(ContenidoMapper.aEntidad(contenido)));
    }

    @Override
    public Optional<ContenidoModulo> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ContenidoMapper::aDominio);
    }

    @Override
    public List<ContenidoModulo> listarPorModulo(Long moduloId) {
        return jpaRepository.buscarPorModulo(moduloId).stream().map(ContenidoMapper::aDominio).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
