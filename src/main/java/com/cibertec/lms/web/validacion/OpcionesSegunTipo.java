package com.cibertec.lms.web.validacion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/** Valida las opciones de una pregunta según su tipo: OPCION_MULTIPLE al menos una y exactamente una correcta,
 *  VERDADERO_FALSO exactamente dos con una correcta, ABIERTA ninguna. El error se reporta en el campo "opciones". */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OpcionesSegunTipoValidator.class)
public @interface OpcionesSegunTipo {

    String message() default "Las opciones no son válidas para el tipo de pregunta";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
