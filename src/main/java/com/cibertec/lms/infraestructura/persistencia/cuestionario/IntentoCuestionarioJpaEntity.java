package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "intentos_cuestionario", uniqueConstraints = @UniqueConstraint(columnNames = {"cuestionario_id", "estudiante_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntentoCuestionarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuestionario_id", nullable = false)
    private Long cuestionarioId;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    @Column(nullable = false)
    private double nota;

    @Column(name = "puntaje_maximo", nullable = false)
    private double puntajeMaximo;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDateTime fechaEntrega;
}
