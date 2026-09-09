package com.cibertec.lms.infraestructura.persistencia.curso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CursoJpaRepository extends JpaRepository<CursoJpaEntity, Long> {

    /** Solo se invoca cuando profesorId no es null; sin filtro se usa el findAll(Pageable) heredado. */
    @Query("SELECT c FROM CursoJpaEntity c WHERE c.profesorId = :profesorId")
    Page<CursoJpaEntity> buscarPorProfesorPaginado(@Param("profesorId") Long profesorId, Pageable pageable);
}
