package com.cibertec.lms.infraestructura.persistencia.actividad;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.actividad.Actividad;
import com.cibertec.lms.dominio.actividad.ActividadRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class ActividadRepositorioImpl implements ActividadRepositorio {

    private final ActividadJpaRepository jpaRepository;

    @Override
    public Actividad guardar(Actividad actividad) {
        return ActividadMapper.aDominio(jpaRepository.save(ActividadMapper.aEntidad(actividad)));
    }

    @Override
    public Optional<Actividad> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ActividadMapper::aDominio);
    }

    @Override
    public List<Actividad> listarPorModulo(Long moduloId) {
        return jpaRepository.buscarPorModulo(moduloId).stream().map(ActividadMapper::aDominio).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
