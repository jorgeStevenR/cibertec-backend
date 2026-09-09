package com.cibertec.lms.infraestructura.persistencia.matricula;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatriculaJpaRepository extends JpaRepository<MatriculaJpaEntity, Long> {

    @Query("SELECT m FROM MatriculaJpaEntity m WHERE m.estudianteId = :estudianteId")
    List<MatriculaJpaEntity> buscarPorEstudiante(@Param("estudianteId") Long estudianteId);

    @Query("SELECT m FROM MatriculaJpaEntity m WHERE m.cursoId = :cursoId")
    List<MatriculaJpaEntity> buscarPorCurso(@Param("cursoId") Long cursoId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MatriculaJpaEntity m WHERE m.cursoId = :cursoId AND m.estudianteId = :estudianteId")
    boolean existePorCursoYEstudiante(@Param("cursoId") Long cursoId, @Param("estudianteId") Long estudianteId);
}
