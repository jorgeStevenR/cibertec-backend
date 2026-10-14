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

    /** Null en respuestas a preguntas ABIERTA. La columna nació NOT NULL: ddl-auto no la relaja solo, se cambió a mano (ver HISTORIAL.md). */
    @Column(name = "opcion_id")
    private Long opcionId;

    /** Texto escrito por el estudiante; solo en respuestas a preguntas ABIERTA. */
    @Column(name = "texto_respuesta", columnDefinition = "text")
    private String textoRespuesta;

    /** Puntaje que asignó el profesor a una respuesta abierta; null mientras no la califique. */
    @Column(name = "puntaje_obtenido")
    private Double puntajeObtenido;
}
