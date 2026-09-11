package com.cibertec.lms.aplicacion.curso;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.almacenamiento.AlmacenamientoPuerto;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Lienzo;
import com.cibertec.lms.dominio.curso.LienzoRepositorio;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.excepcion.LienzoNoEncontradoException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * El bucket de Supabase Storage es privado: {@link AlmacenamientoPuerto#generarUrlDescarga}
 * devuelve URLs firmadas que expiran (ver lms.almacenamiento.url-firmada-minutos). Como una
 * hoja de lienzo se guarda una vez y se muestra indefinidamente, no podemos persistir esa URL
 * firmada tal cual - quedaría rota a los pocos minutos. Por eso, en cada GET de contenido,
 * cualquier "imagenUrl" que apunte a nuestro propio bucket se vuelve a firmar al vuelo antes
 * de responder.
 */
@Service
@RequiredArgsConstructor
public class LienzoService {

    private static final int ANCHO_POR_DEFECTO = 960;
    private static final int ALTO_POR_DEFECTO = 600;

    private final LienzoRepositorio lienzoRepositorio;
    private final AlmacenamientoPuerto almacenamientoPuerto;
    private final ModuloService moduloService;
    private final CursoService cursoService;
    private final MatriculaRepositorio matriculaRepositorio;
    private final ObjectMapper objectMapper;

    @Value("${lms.almacenamiento.endpoint}")
    private String almacenamientoEndpoint;

    @Value("${lms.almacenamiento.bucket}")
    private String almacenamientoBucket;

    public List<Lienzo> listar(Long moduloId, Long usuarioActualId, Rol rolActual) {
        Modulo modulo = moduloService.buscarPorId(moduloId);
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        validarAcceso(curso, usuarioActualId, rolActual);
        return lienzoRepositorio.listarPorModulo(moduloId);
    }

    public Lienzo crear(Long moduloId, String nombre, Long usuarioActualId, Rol rolActual) {
        validarPropietarioDelModulo(moduloId, usuarioActualId, rolActual);

        LocalDateTime ahora = LocalDateTime.now();
        Lienzo nuevo = Lienzo.builder()
                .moduloId(moduloId)
                .nombre(nombre)
                .orden(siguienteOrden(moduloId))
                .ancho(ANCHO_POR_DEFECTO)
                .alto(ALTO_POR_DEFECTO)
                .elementosJson("[]")
                .fechaCreacion(ahora)
                .fechaActualizacion(ahora)
                .build();

        return lienzoRepositorio.guardar(nuevo);
    }

    public Lienzo renombrar(Long lienzoId, String nombre, Long usuarioActualId, Rol rolActual) {
        Lienzo existente = buscarPorId(lienzoId);
        validarPropietarioDelModulo(existente.getModuloId(), usuarioActualId, rolActual);

        Lienzo actualizado = copiarConNombre(existente, nombre);
        return lienzoRepositorio.guardar(actualizado);
    }

    public void eliminar(Long lienzoId, Long usuarioActualId, Rol rolActual) {
        Lienzo existente = buscarPorId(lienzoId);
        validarPropietarioDelModulo(existente.getModuloId(), usuarioActualId, rolActual);
        lienzoRepositorio.eliminar(lienzoId);
    }

    public Lienzo obtenerContenido(Long lienzoId, Long usuarioActualId, Rol rolActual) {
        Lienzo lienzo = buscarPorId(lienzoId);
        Modulo modulo = moduloService.buscarPorId(lienzo.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        validarAcceso(curso, usuarioActualId, rolActual);

        return Lienzo.builder()
                .id(lienzo.getId())
                .moduloId(lienzo.getModuloId())
                .nombre(lienzo.getNombre())
                .orden(lienzo.getOrden())
                .ancho(lienzo.getAncho())
                .alto(lienzo.getAlto())
                .elementosJson(renovarUrlsDeImagenes(lienzo.getElementosJson()))
                .fechaCreacion(lienzo.getFechaCreacion())
                .fechaActualizacion(lienzo.getFechaActualizacion())
                .build();
    }

    public Lienzo guardarContenido(
            Long lienzoId, int ancho, int alto, JsonNode elementos, Long usuarioActualId, Rol rolActual) {
        Lienzo existente = buscarPorId(lienzoId);
        validarPropietarioDelModulo(existente.getModuloId(), usuarioActualId, rolActual);

        if (!elementos.isArray()) {
            throw new ReglaNegocioException("elementos debe ser un arreglo JSON");
        }

        Lienzo actualizado = Lienzo.builder()
                .id(existente.getId())
                .moduloId(existente.getModuloId())
                .nombre(existente.getNombre())
                .orden(existente.getOrden())
                .ancho(ancho)
                .alto(alto)
                .elementosJson(elementos.toString())
                .fechaCreacion(existente.getFechaCreacion())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        return lienzoRepositorio.guardar(actualizado);
    }

    public String subirImagen(
            Long lienzoId, String nombreOriginal, InputStream contenido,
            long tamanio, String tipoContenidoHttp, Long usuarioActualId, Rol rolActual) {
        Lienzo lienzo = buscarPorId(lienzoId);
        Curso curso = validarPropietarioDelModulo(lienzo.getModuloId(), usuarioActualId, rolActual);

        String rutaDestino = "curso-%d/modulo-%d/lienzo-%d/%s-%s".formatted(
                curso.getId(), lienzo.getModuloId(), lienzoId, UUID.randomUUID(), nombreOriginal);
        almacenamientoPuerto.subir(rutaDestino, contenido, tamanio, tipoContenidoHttp);

        return almacenamientoPuerto.generarUrlDescarga(rutaDestino);
    }

    private Lienzo buscarPorId(Long id) {
        return lienzoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new LienzoNoEncontradoException(id));
    }

    private int siguienteOrden(Long moduloId) {
        return lienzoRepositorio.listarPorModulo(moduloId).size() + 1;
    }

    private Lienzo copiarConNombre(Lienzo lienzo, String nombre) {
        return Lienzo.builder()
                .id(lienzo.getId())
                .moduloId(lienzo.getModuloId())
                .nombre(nombre)
                .orden(lienzo.getOrden())
                .ancho(lienzo.getAncho())
                .alto(lienzo.getAlto())
                .elementosJson(lienzo.getElementosJson())
                .fechaCreacion(lienzo.getFechaCreacion())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    private String renovarUrlsDeImagenes(String elementosJson) {
        JsonNode raiz;
        try {
            raiz = objectMapper.readTree(elementosJson);
        } catch (Exception e) {
            return elementosJson;
        }
        if (!raiz.isArray()) {
            return elementosJson;
        }

        for (JsonNode elemento : raiz) {
            if (elemento instanceof ObjectNode objeto) {
                renovarSiEsUrlPropia(objeto, "imagenUrl");
            }
        }
        return raiz.toString();
    }

    private void renovarSiEsUrlPropia(ObjectNode elemento, String campo) {
        JsonNode valor = elemento.get(campo);
        if (valor == null || !valor.isTextual()) {
            return;
        }
        extraerRutaSiEsPropia(valor.asText())
                .ifPresent(ruta -> elemento.put(campo, almacenamientoPuerto.generarUrlDescarga(ruta)));
    }

    private Optional<String> extraerRutaSiEsPropia(String url) {
        String prefijo = almacenamientoEndpoint.replaceAll("/+$", "") + "/" + almacenamientoBucket + "/";
        if (!url.startsWith(prefijo)) {
            return Optional.empty();
        }
        String resto = url.substring(prefijo.length());
        int finRuta = resto.indexOf('?');
        String ruta = finRuta >= 0 ? resto.substring(0, finRuta) : resto;
        return Optional.of(URLDecoder.decode(ruta, StandardCharsets.UTF_8));
    }

    private Curso validarPropietarioDelModulo(Long moduloId, Long usuarioActualId, Rol rolActual) {
        Modulo modulo = moduloService.buscarPorId(moduloId);
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
        return curso;
    }

    private void validarAcceso(Curso curso, Long usuarioActualId, Rol rolActual) {
        if (rolActual == Rol.ESTUDIANTE) {
            if (!matriculaRepositorio.existePorCursoYEstudiante(curso.getId(), usuarioActualId)) {
                throw new AccessDeniedException("No estás matriculado en este curso");
            }
            return;
        }
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
    }
}
