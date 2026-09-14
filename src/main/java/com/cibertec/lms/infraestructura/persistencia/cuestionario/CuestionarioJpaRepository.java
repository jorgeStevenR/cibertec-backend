package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CuestionarioJpaRepository extends JpaRepository<CuestionarioJpaEntity, Long> {

    @Query("SELECT c FROM CuestionarioJpaEntity c WHERE c.moduloId = :moduloId ORDER BY c.fechaCreacion ASC")
    List<CuestionarioJpaEntity> buscarPorModulo(@Param("moduloId") Long moduloId);
}
