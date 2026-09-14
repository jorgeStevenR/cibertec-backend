package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.cuestionario.Opcion;
import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.dominio.cuestionario.PreguntaRepositorio;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class PreguntaRepositorioImpl implements PreguntaRepositorio {

    private final PreguntaJpaRepository preguntaJpaRepository;
    private final OpcionJpaRepository opcionJpaRepository;

    /** Guarda la pregunta y reemplaza sus opciones: las que traen id existente se actualizan,
     *  las que no traen id se insertan, y las que existían pero ya no vienen en la lista se borran. */
    @Override
    public Pregunta guardar(Pregunta pregunta) {
        PreguntaJpaEntity preguntaGuardada = preguntaJpaRepository.save(PreguntaMapper.aEntidad(pregunta));
        Long preguntaId = preguntaGuardada.getId();

        List<OpcionJpaEntity> existentes = opcionJpaRepository.buscarPorPregunta(preguntaId);
        Set<Long> idsExistentes = existentes.stream().map(OpcionJpaEntity::getId).collect(Collectors.toSet());

        Set<Long> idsConservados = new HashSet<>();
        List<OpcionJpaEntity> resultado = new ArrayList<>();

        for (Opcion opcion : pregunta.getOpciones()) {
            OpcionJpaEntity entidad = (opcion.getId() != null && idsExistentes.contains(opcion.getId()))
                    ? existentes.stream().filter(e -> e.getId().equals(opcion.getId())).findFirst().orElseThrow()
                    : new OpcionJpaEntity();

            entidad.setPreguntaId(preguntaId);
            entidad.setTexto(opcion.getTexto());
            entidad.setEsCorrecta(opcion.isEsCorrecta());
            resultado.add(opcionJpaRepository.save(entidad));

            if (opcion.getId() != null) {
                idsConservados.add(opcion.getId());
            }
        }

        existentes.stream()
                .map(OpcionJpaEntity::getId)
                .filter(id -> !idsConservados.contains(id))
                .forEach(opcionJpaRepository::deleteById);

        return PreguntaMapper.aDominio(preguntaGuardada, resultado.stream().map(PreguntaMapper::aDominio).toList());
    }

    @Override
    public Optional<Pregunta> buscarPorId(Long id) {
        return preguntaJpaRepository.findById(id).map(this::conOpciones);
    }

    @Override
    public List<Pregunta> listarPorCuestionario(Long cuestionarioId) {
        return preguntaJpaRepository.buscarPorCuestionario(cuestionarioId).stream().map(this::conOpciones).toList();
    }

    @Override
    public void eliminar(Long id) {
        opcionJpaRepository.buscarPorPregunta(id).forEach(o -> opcionJpaRepository.deleteById(o.getId()));
        preguntaJpaRepository.deleteById(id);
    }

    private Pregunta conOpciones(PreguntaJpaEntity entidad) {
        List<Opcion> opciones = opcionJpaRepository.buscarPorPregunta(entidad.getId()).stream()
                .map(PreguntaMapper::aDominio)
                .toList();
        return PreguntaMapper.aDominio(entidad, opciones);
    }
}
