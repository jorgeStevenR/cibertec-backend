package com.cibertec.lms.infraestructura.persistencia.curso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BloqueJpaRepository extends JpaRepository<BloqueJpaEntity, Long> {

    @Query("SELECT b FROM BloqueJpaEntity b WHERE b.moduloId = :moduloId AND b.paginaId IS NULL ORDER BY b.orden ASC")
    List<BloqueJpaEntity> buscarSueltosPorModulo(@Param("moduloId") Long moduloId);

    @Query("SELECT b FROM BloqueJpaEntity b WHERE b.paginaId = :paginaId ORDER BY b.orden ASC")
    List<BloqueJpaEntity> buscarPorPagina(@Param("paginaId") Long paginaId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BloqueJpaEntity b WHERE b.paginaId = :paginaId")
    void eliminarPorPagina(@Param("paginaId") Long paginaId);
}
