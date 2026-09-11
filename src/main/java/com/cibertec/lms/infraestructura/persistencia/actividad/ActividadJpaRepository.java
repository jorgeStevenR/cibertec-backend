package com.cibertec.lms.infraestructura.persistencia.actividad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActividadJpaRepository extends JpaRepository<ActividadJpaEntity, Long> {

    @Query("SELECT a FROM ActividadJpaEntity a WHERE a.moduloId = :moduloId ORDER BY a.fechaCreacion ASC")
    List<ActividadJpaEntity> buscarPorModulo(@Param("moduloId") Long moduloId);
}
