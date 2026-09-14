package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpcionJpaRepository extends JpaRepository<OpcionJpaEntity, Long> {

    @Query("SELECT o FROM OpcionJpaEntity o WHERE o.preguntaId = :preguntaId")
    List<OpcionJpaEntity> buscarPorPregunta(@Param("preguntaId") Long preguntaId);
}
