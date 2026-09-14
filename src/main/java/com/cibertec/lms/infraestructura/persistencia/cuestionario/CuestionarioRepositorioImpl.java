package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.cuestionario.Cuestionario;
import com.cibertec.lms.dominio.cuestionario.CuestionarioRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class CuestionarioRepositorioImpl implements CuestionarioRepositorio {

    private final CuestionarioJpaRepository jpaRepository;

    @Override
    public Cuestionario guardar(Cuestionario cuestionario) {
        return CuestionarioMapper.aDominio(jpaRepository.save(CuestionarioMapper.aEntidad(cuestionario)));
    }

    @Override
    public Optional<Cuestionario> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(CuestionarioMapper::aDominio);
    }

    @Override
    public List<Cuestionario> listarPorModulo(Long moduloId) {
        return jpaRepository.buscarPorModulo(moduloId).stream().map(CuestionarioMapper::aDominio).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
