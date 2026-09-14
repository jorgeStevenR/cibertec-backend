package com.cibertec.lms.web.validacion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UnaOpcionCorrectaValidator.class)
public @interface UnaOpcionCorrecta {

    String message() default "Debe haber exactamente una opción marcada como correcta";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
