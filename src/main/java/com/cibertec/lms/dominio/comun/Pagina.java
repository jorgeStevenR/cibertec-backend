package com.cibertec.lms.dominio.comun;

import java.util.List;

/** Resultado paginado, propio del dominio (no el Page de Spring Data) para no acoplar el dominio al framework. */
public record Pagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {
}
