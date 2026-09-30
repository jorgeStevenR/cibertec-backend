package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.curso.BloqueContenido;
import com.cibertec.lms.dominio.curso.BloqueRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class BloqueRepositorioImpl implements BloqueRepositorio {

    private final BloqueJpaRepository jpaRepository;

    @Override
    public BloqueContenido guardar(BloqueContenido bloque) {
        return BloqueMapper.aDominio(jpaRepository.save(BloqueMapper.aEntidad(bloque)));
    }

    @Override
    public Optional<BloqueContenido> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(BloqueMapper::aDominio);
    }

    @Override
    public List<BloqueContenido> listarPorModulo(Long moduloId) {
        return jpaRepository.buscarSueltosPorModulo(moduloId).stream().map(BloqueMapper::aDominio).toList();
    }

    @Override
    public List<BloqueContenido> listarPorPagina(Long paginaId) {
        return jpaRepository.buscarPorPagina(paginaId).stream().map(BloqueMapper::aDominio).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void eliminarPorPagina(Long paginaId) {
        jpaRepository.eliminarPorPagina(paginaId);
    }
}
