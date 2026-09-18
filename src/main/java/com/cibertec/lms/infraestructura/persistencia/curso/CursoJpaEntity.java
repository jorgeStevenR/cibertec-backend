package com.cibertec.lms.infraestructura.persistencia.curso;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CursoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(name = "profesor_id", nullable = false)
    private Long profesorId;

    /** Nullable a nivel de columna a propósito: los cursos creados antes de este campo no tienen programa asignado
     *  todavía. La obligatoriedad para cursos nuevos se exige en la validación del DTO, no en el esquema. */
    @Column(name = "programa_id")
    private Long programaId;

    @Column(nullable = false)
    private boolean publicado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /** Los 5 campos siguientes son nullable a nivel de columna por la misma razón que programa_id: hay cursos creados antes
     *  de que existieran. Su obligatoriedad se exige en los DTO/servicio, no en el esquema. El índice único de codigo admite varios NULL. */
    @Column(name = "codigo", length = 30, unique = true)
    private String codigo;

    @Column(name = "periodo", length = 20)
    private String periodo;

    @Column(name = "cupo_maximo")
    private Integer cupoMaximo;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
}
