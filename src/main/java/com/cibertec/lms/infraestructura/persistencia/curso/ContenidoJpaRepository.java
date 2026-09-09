package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContenidoJpaRepository extends JpaRepository<ContenidoJpaEntity, Long> {

    @Query("SELECT c FROM ContenidoJpaEntity c WHERE c.moduloId = :moduloId ORDER BY c.fechaSubida ASC")
    List<ContenidoJpaEntity> buscarPorModulo(@Param("moduloId") Long moduloId);
}
