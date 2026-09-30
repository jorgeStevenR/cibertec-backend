package com.cibertec.lms.infraestructura.persistencia.curso;

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
@Table(name = "paginas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginaModuloJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modulo_id", nullable = false)
    private Long moduloId;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false)
    private int orden;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
