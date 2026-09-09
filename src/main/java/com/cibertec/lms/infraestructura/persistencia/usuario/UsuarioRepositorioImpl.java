package com.cibertec.lms.infraestructura.persistencia.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

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
    public List<Usuario> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(UsuarioMapper::aDominio)
                .toList();
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
