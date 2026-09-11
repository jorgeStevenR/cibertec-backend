package com.cibertec.lms.infraestructura.persistencia.matricula;

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
@Table(name = "matriculas_programa", uniqueConstraints = @UniqueConstraint(columnNames = {"programa_id", "estudiante_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaProgramaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "programa_id", nullable = false)
    private Long programaId;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDateTime fechaMatricula;
}
