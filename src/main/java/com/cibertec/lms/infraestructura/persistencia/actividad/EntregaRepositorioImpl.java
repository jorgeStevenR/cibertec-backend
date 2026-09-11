package com.cibertec.lms.infraestructura.persistencia.actividad;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.actividad.Entrega;
import com.cibertec.lms.dominio.actividad.EntregaRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class EntregaRepositorioImpl implements EntregaRepositorio {

    private final EntregaJpaRepository jpaRepository;

    @Override
    public Entrega guardar(Entrega entrega) {
        return EntregaMapper.aDominio(jpaRepository.save(EntregaMapper.aEntidad(entrega)));
    }

    @Override
    public Optional<Entrega> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(EntregaMapper::aDominio);
    }

    @Override
    public Optional<Entrega> buscarPorActividadYEstudiante(Long actividadId, Long estudianteId) {
        return jpaRepository.findByActividadIdAndEstudianteId(actividadId, estudianteId).map(EntregaMapper::aDominio);
    }

    @Override
    public List<Entrega> listarPorActividad(Long actividadId) {
        return jpaRepository.buscarPorActividad(actividadId).stream().map(EntregaMapper::aDominio).toList();
    }
}
