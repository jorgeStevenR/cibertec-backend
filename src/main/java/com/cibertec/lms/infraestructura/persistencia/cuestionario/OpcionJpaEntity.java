package com.cibertec.lms.infraestructura.persistencia.cuestionario;

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
@Table(name = "opciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpcionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pregunta_id", nullable = false)
    private Long preguntaId;

    @Column(columnDefinition = "text", nullable = false)
    private String texto;

    @Column(name = "es_correcta", nullable = false)
    private boolean esCorrecta;
}
