package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LienzoJpaRepository extends JpaRepository<LienzoJpaEntity, Long> {

    @Query("SELECT l FROM LienzoJpaEntity l WHERE l.moduloId = :moduloId ORDER BY l.orden ASC")
    List<LienzoJpaEntity> buscarPorModulo(@Param("moduloId") Long moduloId);
}
