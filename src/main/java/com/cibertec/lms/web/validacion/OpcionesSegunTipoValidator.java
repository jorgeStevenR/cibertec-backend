package com.cibertec.lms.web.validacion;

import java.util.List;

import com.cibertec.lms.web.dtos.OpcionRequestDTO;
import com.cibertec.lms.web.dtos.PreguntaRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OpcionesSegunTipoValidator implements ConstraintValidator<OpcionesSegunTipo, PreguntaRequestDTO> {

    @Override
    public boolean isValid(PreguntaRequestDTO pregunta, ConstraintValidatorContext context) {
        if (pregunta == null) {
            return true;
        }
        List<OpcionRequestDTO> opciones = pregunta.opciones() != null ? pregunta.opciones() : List.of();
        long correctas = opciones.stream().filter(OpcionRequestDTO::esCorrecta).count();

        String error = switch (pregunta.tipoEfectivo()) {
            case OPCION_MULTIPLE -> opciones.isEmpty() ? "La pregunta debe tener al menos una opción"
                    : correctas != 1 ? "Debe haber exactamente una opción marcada como correcta"
                    : null;
            case VERDADERO_FALSO -> opciones.size() != 2 || correctas != 1
                    ? "Una pregunta de verdadero/falso lleva exactamente dos opciones y una sola correcta"
                    : null;
            case ABIERTA -> opciones.isEmpty() ? null : "Una pregunta abierta no lleva opciones";
        };

        if (error == null) {
            return true;
        }
        // Se cuelga del campo "opciones" para que llegue en "errores.opciones", igual que un error de campo común.
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(error).addPropertyNode("opciones").addConstraintViolation();
        return false;
    }
}
