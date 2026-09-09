package com.cibertec.lms.infraestructura.persistencia.curso;

import com.cibertec.lms.dominio.curso.Curso;

class CursoMapper {

    private CursoMapper() {
    }

    static Curso aDominio(CursoJpaEntity entidad) {
        return Curso.builder()
                .id(entidad.getId())
                .titulo(entidad.getTitulo())
                .descripcion(entidad.getDescripcion())
                .profesorId(entidad.getProfesorId())
                .publicado(entidad.isPublicado())
                .fechaCreacion(entidad.getFechaCreacion())
                .build();
    }

    static CursoJpaEntity aEntidad(Curso curso) {
        CursoJpaEntity entidad = new CursoJpaEntity();
        entidad.setId(curso.getId());
        entidad.setTitulo(curso.getTitulo());
        entidad.setDescripcion(curso.getDescripcion());
        entidad.setProfesorId(curso.getProfesorId());
        entidad.setPublicado(curso.isPublicado());
        entidad.setFechaCreacion(curso.getFechaCreacion());
        return entidad;
    }
}
