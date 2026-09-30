package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaginaModuloJpaRepository extends JpaRepository<PaginaModuloJpaEntity, Long> {

    @Query("SELECT p FROM PaginaModuloJpaEntity p WHERE p.moduloId = :moduloId ORDER BY p.orden ASC, p.id ASC")
    List<PaginaModuloJpaEntity> buscarPorModulo(@Param("moduloId") Long moduloId);
}
