package com.cibertec.lms.dominio.matricula;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Matricula {

    private Long id;
    private Long cursoId;
    private Long estudianteId;
    private LocalDateTime fechaMatricula;
}
