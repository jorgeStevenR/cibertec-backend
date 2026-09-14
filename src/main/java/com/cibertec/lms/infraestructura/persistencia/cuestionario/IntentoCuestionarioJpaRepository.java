package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntentoCuestionarioJpaRepository extends JpaRepository<IntentoCuestionarioJpaEntity, Long> {

    Optional<IntentoCuestionarioJpaEntity> findByCuestionarioIdAndEstudianteId(Long cuestionarioId, Long estudianteId);

    boolean existsByCuestionarioIdAndEstudianteId(Long cuestionarioId, Long estudianteId);

    @Query("SELECT i FROM IntentoCuestionarioJpaEntity i WHERE i.cuestionarioId = :cuestionarioId ORDER BY i.fechaEntrega ASC")
    List<IntentoCuestionarioJpaEntity> buscarPorCuestionario(@Param("cuestionarioId") Long cuestionarioId);
}
