package com.cibertec.lms.infraestructura.persistencia.matricula;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatriculaProgramaJpaRepository extends JpaRepository<MatriculaProgramaJpaEntity, Long> {

    @Query("SELECT m FROM MatriculaProgramaJpaEntity m WHERE m.estudianteId = :estudianteId")
    List<MatriculaProgramaJpaEntity> buscarPorEstudiante(@Param("estudianteId") Long estudianteId);

    @Query("SELECT m FROM MatriculaProgramaJpaEntity m WHERE m.programaId = :programaId")
    List<MatriculaProgramaJpaEntity> buscarPorPrograma(@Param("programaId") Long programaId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MatriculaProgramaJpaEntity m WHERE m.programaId = :programaId AND m.estudianteId = :estudianteId")
    boolean existePorProgramaYEstudiante(@Param("programaId") Long programaId, @Param("estudianteId") Long estudianteId);
}
