package com.cibertec.lms.infraestructura.persistencia.curso;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.curso.TipoBloque;

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
@Table(name = "bloques_contenido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloqueJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modulo_id", nullable = false)
    private Long moduloId;

    @Column(name = "pagina_id")
    private Long paginaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBloque tipo;

    @Column(columnDefinition = "text")
    private String texto;

    @Column(name = "ruta_archivo")
    private String rutaArchivo;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "url_video", columnDefinition = "text")
    private String urlVideo;

    @Column(nullable = false)
    private int orden;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
