package com.cibertec.lms.infraestructura.persistencia.usuario;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.comun.Pagina;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;
import com.cibertec.lms.infraestructura.persistencia.comun.PaginaMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class UsuarioRepositorioImpl implements UsuarioRepositorio {

    private final UsuarioJpaRepository jpaRepository;

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioJpaEntity guardado = jpaRepository.save(UsuarioMapper.aEntidad(usuario));
        return UsuarioMapper.aDominio(guardado);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(UsuarioMapper::aDominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.buscarPorEmail(email).map(UsuarioMapper::aDominio);
    }

    @Override
    public Pagina<Usuario> listarPaginado(int pagina, int tamano, String busqueda) {
        PageRequest pageRequest = PageRequest.of(pagina, tamano);
        var page = (busqueda == null || busqueda.isBlank())
                ? jpaRepository.findAll(pageRequest)
                : jpaRepository.buscarPaginado(busqueda, pageRequest);
        return PaginaMapper.aDominio(page, UsuarioMapper::aDominio);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existePorEmail(email);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
