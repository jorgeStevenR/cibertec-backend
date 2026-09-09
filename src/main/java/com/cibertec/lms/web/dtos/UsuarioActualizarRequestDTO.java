package com.cibertec.lms.web.dtos;

import com.cibertec.lms.dominio.usuario.Rol;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioActualizarRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotNull(message = "El rol es obligatorio")
        Rol rol,

        boolean activo
) {
}
