package com.cibertec.lms.infraestructura.persistencia.programa;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.comun.Pagina;
import com.cibertec.lms.dominio.programa.Programa;
import com.cibertec.lms.dominio.programa.ProgramaRepositorio;
import com.cibertec.lms.infraestructura.persistencia.comun.PaginaMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class ProgramaRepositorioImpl implements ProgramaRepositorio {

    private final ProgramaJpaRepository jpaRepository;

    @Override
    public Programa guardar(Programa programa) {
        return ProgramaMapper.aDominio(jpaRepository.save(ProgramaMapper.aEntidad(programa)));
    }

    @Override
    public Optional<Programa> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ProgramaMapper::aDominio);
    }

    @Override
    public Map<Long, Programa> buscarPorIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return jpaRepository.findAllById(ids).stream()
                .map(ProgramaMapper::aDominio)
                .collect(Collectors.toMap(Programa::getId, p -> p));
    }

    @Override
    public Pagina<Programa> listarPaginado(int pagina, int tamano, String busqueda) {
        PageRequest pageRequest = PageRequest.of(pagina, tamano);
        var page = (busqueda == null || busqueda.isBlank())
                ? jpaRepository.findAll(pageRequest)
                : jpaRepository.buscarPaginado(busqueda, pageRequest);
        return PaginaMapper.aDominio(page, ProgramaMapper::aDominio);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
