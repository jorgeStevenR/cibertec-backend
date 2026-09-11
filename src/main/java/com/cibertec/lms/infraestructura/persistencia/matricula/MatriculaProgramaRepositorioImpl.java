package com.cibertec.lms.infraestructura.persistencia.matricula;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.matricula.MatriculaPrograma;
import com.cibertec.lms.dominio.matricula.MatriculaProgramaRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class MatriculaProgramaRepositorioImpl implements MatriculaProgramaRepositorio {

    private final MatriculaProgramaJpaRepository jpaRepository;

    @Override
    public MatriculaPrograma guardar(MatriculaPrograma matricula) {
        return MatriculaProgramaMapper.aDominio(jpaRepository.save(MatriculaProgramaMapper.aEntidad(matricula)));
    }

    @Override
    public Optional<MatriculaPrograma> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(MatriculaProgramaMapper::aDominio);
    }

    @Override
    public List<MatriculaPrograma> listarPorEstudiante(Long estudianteId) {
        return jpaRepository.buscarPorEstudiante(estudianteId).stream().map(MatriculaProgramaMapper::aDominio).toList();
    }

    @Override
    public List<MatriculaPrograma> listarPorPrograma(Long programaId) {
        return jpaRepository.buscarPorPrograma(programaId).stream().map(MatriculaProgramaMapper::aDominio).toList();
    }

    @Override
    public boolean existePorProgramaYEstudiante(Long programaId, Long estudianteId) {
        return jpaRepository.existePorProgramaYEstudiante(programaId, estudianteId);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
