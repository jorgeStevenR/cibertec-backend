package com.cibertec.lms.infraestructura.persistencia.actividad;

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
@Table(name = "entregas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntregaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actividad_id", nullable = false)
    private Long actividadId;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "ruta_archivo", nullable = false)
    private String rutaArchivo;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDateTime fechaEntrega;

    private Double nota;

    @Column(name = "comentario_docente", columnDefinition = "text")
    private String comentarioDocente;

    @Column(name = "fecha_calificacion")
    private LocalDateTime fechaCalificacion;
}
