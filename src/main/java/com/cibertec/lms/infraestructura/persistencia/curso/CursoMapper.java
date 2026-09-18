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
                .programaId(entidad.getProgramaId())
                .publicado(entidad.isPublicado())
                .fechaCreacion(entidad.getFechaCreacion())
                .codigo(entidad.getCodigo())
                .periodo(entidad.getPeriodo())
                .cupoMaximo(entidad.getCupoMaximo())
                .fechaInicio(entidad.getFechaInicio())
                .fechaFin(entidad.getFechaFin())
                .build();
    }

    static CursoJpaEntity aEntidad(Curso curso) {
        CursoJpaEntity entidad = new CursoJpaEntity();
        entidad.setId(curso.getId());
        entidad.setTitulo(curso.getTitulo());
        entidad.setDescripcion(curso.getDescripcion());
        entidad.setProfesorId(curso.getProfesorId());
        entidad.setProgramaId(curso.getProgramaId());
        entidad.setPublicado(curso.isPublicado());
        entidad.setFechaCreacion(curso.getFechaCreacion());
        entidad.setCodigo(curso.getCodigo());
        entidad.setPeriodo(curso.getPeriodo());
        entidad.setCupoMaximo(curso.getCupoMaximo());
        entidad.setFechaInicio(curso.getFechaInicio());
        entidad.setFechaFin(curso.getFechaFin());
        return entidad;
    }
}
