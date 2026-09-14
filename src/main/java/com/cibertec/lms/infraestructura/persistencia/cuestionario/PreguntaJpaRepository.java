package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PreguntaJpaRepository extends JpaRepository<PreguntaJpaEntity, Long> {

    @Query("SELECT p FROM PreguntaJpaEntity p WHERE p.cuestionarioId = :cuestionarioId ORDER BY p.orden ASC")
    List<PreguntaJpaEntity> buscarPorCuestionario(@Param("cuestionarioId") Long cuestionarioId);
}
