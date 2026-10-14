package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RespuestaIntentoJpaRepository extends JpaRepository<RespuestaIntentoJpaEntity, Long> {

    @Query("SELECT r FROM RespuestaIntentoJpaEntity r WHERE r.intentoId = :intentoId")
    List<RespuestaIntentoJpaEntity> buscarPorIntento(@Param("intentoId") Long intentoId);

    @Query("SELECT r FROM RespuestaIntentoJpaEntity r WHERE r.intentoId = :intentoId AND r.preguntaId = :preguntaId")
    Optional<RespuestaIntentoJpaEntity> buscarPorIntentoYPregunta(@Param("intentoId") Long intentoId, @Param("preguntaId") Long preguntaId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RespuestaIntentoJpaEntity r WHERE r.intentoId = :intentoId")
    void eliminarPorIntento(@Param("intentoId") Long intentoId);
}
