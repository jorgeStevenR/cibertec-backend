package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BloqueJpaRepository extends JpaRepository<BloqueJpaEntity, Long> {

    @Query("SELECT b FROM BloqueJpaEntity b WHERE b.moduloId = :moduloId ORDER BY b.orden ASC")
    List<BloqueJpaEntity> buscarPorModulo(@Param("moduloId") Long moduloId);
}
