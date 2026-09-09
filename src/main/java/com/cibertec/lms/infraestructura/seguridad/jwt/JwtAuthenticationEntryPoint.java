package com.cibertec.lms.infraestructura.seguridad.jwt;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Responde 401 (no 403) cuando falta un token válido, para que el frontend distinga "inicia sesión" de "no tienes permiso". */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String cuerpo = """
                {"timestamp":"%s","status":401,"error":"Unauthorized","message":"Se requiere autenticación","path":"%s"}
                """.formatted(LocalDateTime.now(), request.getRequestURI());
        response.getWriter().write(cuerpo);
    }
}
