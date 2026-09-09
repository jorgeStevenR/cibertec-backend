package com.cibertec.lms.dominio.almacenamiento;

import java.io.InputStream;

/** Puerto de salida: sube/borra/genera enlaces de descarga sin acoplar el dominio a Supabase Storage ni a S3. */
public interface AlmacenamientoPuerto {

    /** Sube el contenido y devuelve la ruta (key) con la que quedó guardado en el bucket. */
    String subir(String rutaDestino, InputStream contenido, long tamanio, String tipoContenido);

    /** Genera una URL de descarga temporal (expira sola). */
    String generarUrlDescarga(String ruta);

    void eliminar(String ruta);
}
