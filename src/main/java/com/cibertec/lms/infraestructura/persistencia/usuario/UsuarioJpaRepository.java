package com.cibertec.lms.infraestructura.persistencia.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.lms.dominio.usuario.Rol;

/** JPQL en lugar de SQL nativo a propósito: las consultas quedan portables entre motores (Postgres/Supabase hoy, cualquier otro mañana). */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.email = :email")
    Optional<UsuarioJpaEntity> buscarPorEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UsuarioJpaEntity u WHERE u.email = :email")
    boolean existePorEmail(@Param("email") String email);

    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.rol = :rol")
    List<UsuarioJpaEntity> buscarPorRol(@Param("rol") Rol rol);

    /** Solo se invoca cuando busqueda no es null/blank; para el caso sin filtro se usa el findAll(Pageable) heredado. */
    @Query("""
            SELECT u FROM UsuarioJpaEntity u
            WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%'))
            """)
    Page<UsuarioJpaEntity> buscarPaginado(@Param("busqueda") String busqueda, Pageable pageable);
}
