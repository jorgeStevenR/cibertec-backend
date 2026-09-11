package com.cibertec.lms.infraestructura.persistencia.programa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProgramaJpaRepository extends JpaRepository<ProgramaJpaEntity, Long> {

    /** Solo se invoca cuando busqueda no es null/blank; para el caso sin filtro se usa el findAll(Pageable) heredado. */
    @Query("""
            SELECT p FROM ProgramaJpaEntity p
            WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%'))
            """)
    Page<ProgramaJpaEntity> buscarPaginado(@Param("busqueda") String busqueda, Pageable pageable);
}
