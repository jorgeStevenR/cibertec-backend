package com.cibertec.lms.infraestructura.seguridad.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.infraestructura.seguridad.UsuarioAutenticado;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String encabezado = request.getHeader("Authorization");

        if (encabezado != null && encabezado.startsWith(PREFIJO_BEARER)) {
            String token = encabezado.substring(PREFIJO_BEARER.length());
            Claims claims = jwtService.validarYExtraerClaims(token);

            if (claims != null) {
                Long id = jwtService.extraerId(claims);
                String email = jwtService.extraerEmail(claims);
                Rol rol = jwtService.extraerRol(claims);

                UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado(id, email, rol);
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
                var authentication = new UsernamePasswordAuthenticationToken(usuarioAutenticado, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
