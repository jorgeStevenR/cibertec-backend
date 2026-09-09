package com.cibertec.lms.infraestructura.persistencia.matricula;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.matricula.Matricula;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class MatriculaRepositorioImpl implements MatriculaRepositorio {

    private final MatriculaJpaRepository jpaRepository;

    @Override
    public Matricula guardar(Matricula matricula) {
        return MatriculaMapper.aDominio(jpaRepository.save(MatriculaMapper.aEntidad(matricula)));
    }

    @Override
    public Optional<Matricula> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(MatriculaMapper::aDominio);
    }

    @Override
    public List<Matricula> listarPorEstudiante(Long estudianteId) {
        return jpaRepository.buscarPorEstudiante(estudianteId).stream().map(MatriculaMapper::aDominio).toList();
    }

    @Override
    public List<Matricula> listarPorCurso(Long cursoId) {
        return jpaRepository.buscarPorCurso(cursoId).stream().map(MatriculaMapper::aDominio).toList();
    }

    @Override
    public boolean existePorCursoYEstudiante(Long cursoId, Long estudianteId) {
        return jpaRepository.existePorCursoYEstudiante(cursoId, estudianteId);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
