package com.cibertec.lms.aplicacion.matricula;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.aplicacion.programa.ProgramaService;
import com.cibertec.lms.dominio.excepcion.MatriculaProgramaNoEncontradaException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.matricula.MatriculaPrograma;
import com.cibertec.lms.dominio.matricula.MatriculaProgramaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatriculaProgramaService {

    private final MatriculaProgramaRepositorio matriculaProgramaRepositorio;
    private final ProgramaService programaService;
    private final UsuarioRepositorio usuarioRepositorio;

    public MatriculaPrograma matricular(Long programaId, Long estudianteId) {
        programaService.buscarPorId(programaId);

        Usuario estudiante = usuarioRepositorio.buscarPorId(estudianteId)
                .orElseThrow(() -> new ReglaNegocioException("El estudiante indicado no existe"));

        if (estudiante.getRol() != Rol.ESTUDIANTE) {
            throw new ReglaNegocioException("El usuario indicado no tiene rol ESTUDIANTE");
        }

        if (matriculaProgramaRepositorio.existePorProgramaYEstudiante(programaId, estudianteId)) {
            throw new ReglaNegocioException("El estudiante ya está matriculado en este programa");
        }

        MatriculaPrograma matricula = MatriculaPrograma.builder()
                .programaId(programaId)
                .estudianteId(estudianteId)
                .fechaMatricula(LocalDateTime.now())
                .build();

        return matriculaProgramaRepositorio.guardar(matricula);
    }

    public List<MatriculaPrograma> listarPorEstudiante(Long estudianteId, Long usuarioActualId, Rol rolActual) {
        if (rolActual == Rol.ESTUDIANTE && !estudianteId.equals(usuarioActualId)) {
            throw new AccessDeniedException("Solo puedes ver tus propias matrículas");
        }
        return matriculaProgramaRepositorio.listarPorEstudiante(estudianteId);
    }

    public List<MatriculaPrograma> listarPorPrograma(Long programaId) {
        programaService.buscarPorId(programaId);
        return matriculaProgramaRepositorio.listarPorPrograma(programaId);
    }

    public void eliminar(Long id) {
        matriculaProgramaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new MatriculaProgramaNoEncontradaException(id));
        matriculaProgramaRepositorio.eliminar(id);
    }
}
