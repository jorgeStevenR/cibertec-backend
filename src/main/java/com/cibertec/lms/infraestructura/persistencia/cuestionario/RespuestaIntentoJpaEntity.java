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
@Table(name = "respuestas_intento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaIntentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intento_id", nullable = false)
    private Long intentoId;

    @Column(name = "pregunta_id", nullable = false)
    private Long preguntaId;

    @Column(name = "opcion_id", nullable = false)
    private Long opcionId;
}
