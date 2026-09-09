package com.cibertec.lms.infraestructura.persistencia.matricula;

import com.cibertec.lms.dominio.matricula.Matricula;

class MatriculaMapper {

    private MatriculaMapper() {
    }

    static Matricula aDominio(MatriculaJpaEntity entidad) {
        return Matricula.builder()
                .id(entidad.getId())
                .cursoId(entidad.getCursoId())
                .estudianteId(entidad.getEstudianteId())
                .fechaMatricula(entidad.getFechaMatricula())
                .build();
    }

    static MatriculaJpaEntity aEntidad(Matricula matricula) {
        MatriculaJpaEntity entidad = new MatriculaJpaEntity();
        entidad.setId(matricula.getId());
        entidad.setCursoId(matricula.getCursoId());
        entidad.setEstudianteId(matricula.getEstudianteId());
        entidad.setFechaMatricula(matricula.getFechaMatricula());
        return entidad;
    }
}
