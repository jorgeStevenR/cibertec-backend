package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.curso.PaginaModulo;
import com.cibertec.lms.dominio.curso.PaginaModuloRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class PaginaModuloRepositorioImpl implements PaginaModuloRepositorio {

    private final PaginaModuloJpaRepository jpaRepository;

    @Override
    public PaginaModulo guardar(PaginaModulo pagina) {
        return PaginaModuloMapper.aDominio(jpaRepository.save(PaginaModuloMapper.aEntidad(pagina)));
    }

    @Override
    public Optional<PaginaModulo> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(PaginaModuloMapper::aDominio);
    }

    @Override
    public List<PaginaModulo> listarPorModulo(Long moduloId) {
        return jpaRepository.buscarPorModulo(moduloId).stream().map(PaginaModuloMapper::aDominio).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
