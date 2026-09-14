package com.cibertec.lms.dominio.cuestionario;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class IntentoCuestionario {

    private Long id;
    private Long cuestionarioId;
    private Long estudianteId;
    private double nota;

    /** Copia del puntaje máximo al momento de responder: si el profesor edita las preguntas después,
     *  este intento ya calificado no cambia de escala retroactivamente. */
    private double puntajeMaximo;

    private LocalDateTime fechaEntrega;
    private List<RespuestaValor> respuestas;
}
