package com.cibertec.lms.web.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.cibertec.lms.dominio.excepcion.CredencialesInvalidasException;
import com.cibertec.lms.dominio.excepcion.GestionCuentaNoPermitidaException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.excepcion.RecursoNoEncontradoException;
import com.cibertec.lms.dominio.excepcion.VentanaCuestionarioException;
import com.cibertec.lms.web.dtos.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarNoEncontrado(RecursoNoEncontradoException ex, WebRequest request) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> manejarReglaNegocio(ReglaNegocioException ex, WebRequest request) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponseDTO> manejarCredencialesInvalidas(CredencialesInvalidasException ex, WebRequest request) {
        return construir(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> manejarAccesoDenegado(AccessDeniedException ex, WebRequest request) {
        return construir(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción", request);
    }

    @ExceptionHandler(VentanaCuestionarioException.class)
    public ResponseEntity<ErrorResponseDTO> manejarVentanaCuestionario(VentanaCuestionarioException ex, WebRequest request) {
        return construir(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(GestionCuentaNoPermitidaException.class)
    public ResponseEntity<ErrorResponseDTO> manejarGestionCuentaNoPermitida(GestionCuentaNoPermitidaException ex, WebRequest request) {
        return construir(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidacion(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));

        ErrorResponseDTO body = ErrorResponseDTO.deValidacion(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Uno o más campos no son válidos",
                ruta(request),
                errores
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorGeneral(Exception ex, WebRequest request) {
        log.error("Error inesperado procesando la petición", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado", request);
    }

    private ResponseEntity<ErrorResponseDTO> construir(HttpStatus status, String mensaje, WebRequest request) {
        ErrorResponseDTO body = ErrorResponseDTO.de(status.value(), status.getReasonPhrase(), mensaje, ruta(request));
        return ResponseEntity.status(status).body(body);
    }

    private String ruta(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
