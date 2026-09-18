package com.cibertec.lms.infraestructura.persistencia.actividad;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.lms.infraestructura.persistencia.estudiante.ActividadAvanceFila;

public interface ActividadJpaRepository extends JpaRepository<ActividadJpaEntity, Long> {

    /** Una sola consulta para todas las actividades de los cursos dados, con la entrega del estudiante (LEFT JOIN: null si no entregó). */
    @Query("""
            SELECT new com.cibertec.lms.infraestructura.persistencia.estudiante.ActividadAvanceFila(
                a.id, a.titulo, a.fechaEntrega, a.puntajeMaximo, a.fechaCreacion,
                mo.id, mo.titulo, mo.orden, mo.cursoId,
                e.fechaEntrega, e.nota, e.fechaCalificacion, e.comentarioDocente)
            FROM ActividadJpaEntity a
            JOIN ModuloJpaEntity mo ON mo.id = a.moduloId
            LEFT JOIN EntregaJpaEntity e ON e.actividadId = a.id AND e.estudianteId = :estudianteId
            WHERE mo.cursoId IN :cursoIds
            ORDER BY mo.orden ASC, a.fechaCreacion ASC
            """)
    List<ActividadAvanceFila> buscarAvanceDelEstudiante(
            @Param("estudianteId") Long estudianteId, @Param("cursoIds") Collection<Long> cursoIds);

    @Query("SELECT a FROM ActividadJpaEntity a WHERE a.moduloId = :moduloId ORDER BY a.fechaCreacion ASC")
    List<ActividadJpaEntity> buscarPorModulo(@Param("moduloId") Long moduloId);
}
