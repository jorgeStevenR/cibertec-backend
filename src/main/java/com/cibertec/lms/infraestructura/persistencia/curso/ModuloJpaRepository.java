package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModuloJpaRepository extends JpaRepository<ModuloJpaEntity, Long> {

    @Query("SELECT m FROM ModuloJpaEntity m WHERE m.cursoId = :cursoId ORDER BY m.orden ASC")
    List<ModuloJpaEntity> buscarPorCursoOrdenado(@Param("cursoId") Long cursoId);
}
