package com.cibertec.lms.infraestructura.persistencia.matricula;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.lms.infraestructura.persistencia.estudiante.CursoMatriculadoFila;

public interface MatriculaJpaRepository extends JpaRepository<MatriculaJpaEntity, Long> {

    @Query("""
            SELECT new com.cibertec.lms.infraestructura.persistencia.estudiante.CursoMatriculadoFila(c.id, c.titulo, c.fechaInicio, c.fechaFin)
            FROM MatriculaJpaEntity m
            JOIN CursoJpaEntity c ON c.id = m.cursoId
            WHERE m.estudianteId = :estudianteId AND c.publicado = true
            ORDER BY c.titulo ASC
            """)
    List<CursoMatriculadoFila> buscarCursosPublicadosDelEstudiante(@Param("estudianteId") Long estudianteId);

    @Query("SELECT m FROM MatriculaJpaEntity m WHERE m.estudianteId = :estudianteId")
    List<MatriculaJpaEntity> buscarPorEstudiante(@Param("estudianteId") Long estudianteId);

    @Query("SELECT m FROM MatriculaJpaEntity m WHERE m.cursoId = :cursoId")
    List<MatriculaJpaEntity> buscarPorCurso(@Param("cursoId") Long cursoId);

    @Query("SELECT COUNT(m) FROM MatriculaJpaEntity m WHERE m.cursoId = :cursoId")
    long contarPorCurso(@Param("cursoId") Long cursoId);

    @Query("""
            SELECT new com.cibertec.lms.infraestructura.persistencia.matricula.ConteoMatriculasFila(m.cursoId, COUNT(m))
            FROM MatriculaJpaEntity m
            WHERE m.cursoId IN :cursoIds
            GROUP BY m.cursoId
            """)
    List<ConteoMatriculasFila> contarPorCursos(@Param("cursoIds") Collection<Long> cursoIds);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MatriculaJpaEntity m WHERE m.cursoId = :cursoId AND m.estudianteId = :estudianteId")
    boolean existePorCursoYEstudiante(@Param("cursoId") Long cursoId, @Param("estudianteId") Long estudianteId);
}
