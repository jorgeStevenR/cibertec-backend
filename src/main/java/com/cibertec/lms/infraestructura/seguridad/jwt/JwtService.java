package com.cibertec.lms.infraestructura.seguridad.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cibertec.lms.dominio.auth.TokenProveedor;
import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtService implements TokenProveedor {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(
            @Value("${lms.jwt.secret}") String secreto,
            @Value("${lms.jwt.expiracion-minutos:120}") long expiracionMinutos) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMinutos * 60_000;
    }

    @Override
    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(clave)
                .compact();
    }

    public Claims validarYExtraerClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Token JWT inválido: {}", ex.getMessage());
            return null;
        }
    }

    public Long extraerId(Claims claims) {
        return claims.get("id", Long.class);
    }

    public String extraerEmail(Claims claims) {
        return claims.getSubject();
    }

    public Rol extraerRol(Claims claims) {
        return Rol.valueOf(claims.get("rol", String.class));
    }
}
