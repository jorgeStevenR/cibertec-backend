package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CursoJpaRepository extends JpaRepository<CursoJpaEntity, Long> {

    @Query("SELECT c FROM CursoJpaEntity c WHERE c.profesorId = :profesorId")
    List<CursoJpaEntity> buscarPorProfesor(@Param("profesorId") Long profesorId);
}
