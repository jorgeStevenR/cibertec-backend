package com.cibertec.lms.web.dtos;

import java.util.List;

public record PaginaResponseDTO<T>(
        List<T> contenido,
        int pagina,
        int tamano,
        long totalElementos,
        int totalPaginas
) {
}
