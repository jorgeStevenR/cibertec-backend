package com.cibertec.lms.infraestructura.persistencia.comun;

import java.util.function.Function;

import org.springframework.data.domain.Page;

import com.cibertec.lms.dominio.comun.Pagina;

/** Convierte el Page de Spring Data (infraestructura) al Pagina propio del dominio. */
public class PaginaMapper {

    private PaginaMapper() {
    }

    public static <E, D> Pagina<D> aDominio(Page<E> page, Function<E, D> mapeador) {
        return new Pagina<>(
                page.getContent().stream().map(mapeador).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
