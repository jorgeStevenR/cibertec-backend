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

    /** Solo se invoca cuando programaId no es null (y profesorId sí lo es). */
    @Query("SELECT c FROM CursoJpaEntity c WHERE c.programaId = :programaId")
    Page<CursoJpaEntity> buscarPorProgramaPaginado(@Param("programaId") Long programaId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CursoJpaEntity c WHERE c.programaId = :programaId")
    boolean existePorProgramaId(@Param("programaId") Long programaId);
}
