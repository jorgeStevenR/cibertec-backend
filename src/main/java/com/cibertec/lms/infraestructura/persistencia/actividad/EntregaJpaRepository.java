package com.cibertec.lms.infraestructura.persistencia.actividad;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EntregaJpaRepository extends JpaRepository<EntregaJpaEntity, Long> {

    @Query("SELECT e FROM EntregaJpaEntity e WHERE e.actividadId = :actividadId ORDER BY e.fechaEntrega ASC")
    List<EntregaJpaEntity> buscarPorActividad(@Param("actividadId") Long actividadId);

    Optional<EntregaJpaEntity> findByActividadIdAndEstudianteId(Long actividadId, Long estudianteId);
}
