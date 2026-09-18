package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface CursoJpaRepository extends JpaRepository<CursoJpaEntity, Long> {

    /** SELECT ... FOR UPDATE: solo tiene sentido dentro de una transacción (el bloqueo dura hasta su commit/rollback). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CursoJpaEntity c WHERE c.id = :id")
    Optional<CursoJpaEntity> buscarPorIdBloqueando(@Param("id") Long id);

    @Query("SELECT c FROM CursoJpaEntity c WHERE UPPER(c.codigo) = UPPER(:codigo)")
    Optional<CursoJpaEntity> buscarPorCodigo(@Param("codigo") String codigo);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CursoJpaEntity c WHERE UPPER(c.codigo) = UPPER(:codigo)")
    boolean existePorCodigo(@Param("codigo") String codigo);

    /** Solo se invoca cuando profesorId no es null; sin filtro se usa el findAll(Pageable) heredado. */
    @Query("SELECT c FROM CursoJpaEntity c WHERE c.profesorId = :profesorId")
    Page<CursoJpaEntity> buscarPorProfesorPaginado(@Param("profesorId") Long profesorId, Pageable pageable);

    /** Solo se invoca cuando programaId no es null (y profesorId sí lo es). */
    @Query("SELECT c FROM CursoJpaEntity c WHERE c.programaId = :programaId")
    Page<CursoJpaEntity> buscarPorProgramaPaginado(@Param("programaId") Long programaId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CursoJpaEntity c WHERE c.programaId = :programaId")
    boolean existePorProgramaId(@Param("programaId") Long programaId);
}
