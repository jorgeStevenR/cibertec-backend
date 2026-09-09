package com.cibertec.lms.infraestructura.persistencia.curso;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.curso.TipoContenido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contenidos_modulo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modulo_id", nullable = false)
    private Long moduloId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContenido tipo;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "ruta_archivo")
    private String rutaArchivo;

    @Column(columnDefinition = "text")
    private String url;

    @Column(nullable = false, columnDefinition = "boolean not null default true")
    private boolean visible;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;
}
