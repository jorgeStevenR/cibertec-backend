package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.lms.infraestructura.persistencia.estudiante.CuestionarioAvanceFila;

public interface CuestionarioJpaRepository extends JpaRepository<CuestionarioJpaEntity, Long> {

    /** Una sola consulta para todos los cuestionarios de los cursos dados: el intento del estudiante (LEFT JOIN, null si no
     *  rindió) y la suma actual de puntajes de sus preguntas (subconsulta). Nunca selecciona opciones ni esCorrecta. */
    @Query("""
            SELECT new com.cibertec.lms.infraestructura.persistencia.estudiante.CuestionarioAvanceFila(
                q.id, q.titulo, q.fechaEntrega,
                (SELECT COALESCE(SUM(p.puntaje), 0) FROM PreguntaJpaEntity p WHERE p.cuestionarioId = q.id),
                q.fechaCreacion,
                mo.id, mo.titulo, mo.orden, mo.cursoId,
                i.fechaEntrega, i.nota, i.puntajeMaximo, i.estado)
            FROM CuestionarioJpaEntity q
            JOIN ModuloJpaEntity mo ON mo.id = q.moduloId
            LEFT JOIN IntentoCuestionarioJpaEntity i ON i.cuestionarioId = q.id AND i.estudianteId = :estudianteId
            WHERE mo.cursoId IN :cursoIds
            ORDER BY mo.orden ASC, q.fechaCreacion ASC
            """)
    List<CuestionarioAvanceFila> buscarAvanceDelEstudiante(
            @Param("estudianteId") Long estudianteId, @Param("cursoIds") Collection<Long> cursoIds);

    @Query("SELECT c FROM CuestionarioJpaEntity c WHERE c.moduloId = :moduloId ORDER BY c.fechaCreacion ASC")
    List<CuestionarioJpaEntity> buscarPorModulo(@Param("moduloId") Long moduloId);
}
