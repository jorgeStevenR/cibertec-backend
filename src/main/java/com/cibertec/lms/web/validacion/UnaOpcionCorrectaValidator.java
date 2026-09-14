package com.cibertec.lms.web.validacion;

import java.util.List;

import com.cibertec.lms.web.dtos.OpcionRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UnaOpcionCorrectaValidator implements ConstraintValidator<UnaOpcionCorrecta, List<OpcionRequestDTO>> {

    @Override
    public boolean isValid(List<OpcionRequestDTO> opciones, ConstraintValidatorContext context) {
        if (opciones == null) {
            return true;
        }
        long correctas = opciones.stream().filter(OpcionRequestDTO::esCorrecta).count();
        return correctas == 1;
    }
}
