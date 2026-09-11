package com.cibertec.lms.infraestructura.persistencia.matricula;

import com.cibertec.lms.dominio.matricula.MatriculaPrograma;

class MatriculaProgramaMapper {

    private MatriculaProgramaMapper() {
    }

    static MatriculaPrograma aDominio(MatriculaProgramaJpaEntity entidad) {
        return MatriculaPrograma.builder()
                .id(entidad.getId())
                .programaId(entidad.getProgramaId())
                .estudianteId(entidad.getEstudianteId())
                .fechaMatricula(entidad.getFechaMatricula())
                .build();
    }

    static MatriculaProgramaJpaEntity aEntidad(MatriculaPrograma matricula) {
        MatriculaProgramaJpaEntity entidad = new MatriculaProgramaJpaEntity();
        entidad.setId(matricula.getId());
        entidad.setProgramaId(matricula.getProgramaId());
        entidad.setEstudianteId(matricula.getEstudianteId());
        entidad.setFechaMatricula(matricula.getFechaMatricula());
        return entidad;
    }
}
