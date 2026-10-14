package com.cibertec.lms.infraestructura.persistencia.estudiante;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.cuestionario.EstadoIntento;
import com.cibertec.lms.dominio.estudiante.ActividadDeEstudiante;
import com.cibertec.lms.dominio.estudiante.AvanceEstudianteRepositorio;
import com.cibertec.lms.dominio.estudiante.CuestionarioDeEstudiante;
import com.cibertec.lms.dominio.estudiante.CursoDeEstudiante;
import com.cibertec.lms.infraestructura.persistencia.actividad.ActividadJpaRepository;
import com.cibertec.lms.infraestructura.persistencia.cuestionario.CuestionarioJpaRepository;
import com.cibertec.lms.infraestructura.persistencia.matricula.MatriculaJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class AvanceEstudianteRepositorioImpl implements AvanceEstudianteRepositorio {

    private final MatriculaJpaRepository matriculaJpaRepository;
    private final ActividadJpaRepository actividadJpaRepository;
    private final CuestionarioJpaRepository cuestionarioJpaRepository;

    @Override
    public List<CursoDeEstudiante> listarCursosPublicadosMatriculados(Long estudianteId) {
        return matriculaJpaRepository.buscarCursosPublicadosDelEstudiante(estudianteId).stream()
                .map(fila -> new CursoDeEstudiante(fila.id(), fila.titulo(), fila.fechaInicio(), fila.fechaFin()))
                .toList();
    }

    @Override
    public List<ActividadDeEstudiante> listarActividades(Long estudianteId, Collection<Long> cursoIds) {
        if (cursoIds.isEmpty()) {
            return List.of();
        }
        return actividadJpaRepository.buscarAvanceDelEstudiante(estudianteId, cursoIds).stream()
                .map(f -> new ActividadDeEstudiante(
                        f.id(), f.titulo(), f.fechaLimite(), f.puntajeMaximo(), f.fechaCreacion(),
                        f.moduloId(), f.moduloTitulo(), f.moduloOrden(), f.cursoId(),
                        f.fechaEntrega(), f.nota(), f.fechaCalificacion(), f.comentarioDocente()))
                .toList();
    }

    @Override
    public List<CuestionarioDeEstudiante> listarCuestionarios(Long estudianteId, Collection<Long> cursoIds) {
        if (cursoIds.isEmpty()) {
            return List.of();
        }
        return cuestionarioJpaRepository.buscarAvanceDelEstudiante(estudianteId, cursoIds).stream()
                .map(f -> new CuestionarioDeEstudiante(
                        f.id(), f.titulo(), f.fechaLimite(), f.puntajeMaximoActual().doubleValue(), f.fechaCreacion(),
                        f.moduloId(), f.moduloTitulo(), f.moduloOrden(), f.cursoId(),
                        f.fechaRendicion(), f.nota(), f.puntajeMaximoIntento(),
                        f.estadoIntento() == EstadoIntento.PENDIENTE_REVISION))
                .toList();
    }
}
