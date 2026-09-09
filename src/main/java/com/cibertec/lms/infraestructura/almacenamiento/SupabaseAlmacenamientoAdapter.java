package com.cibertec.lms.infraestructura.almacenamiento;

import java.io.InputStream;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cibertec.lms.dominio.almacenamiento.AlmacenamientoPuerto;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@RequiredArgsConstructor
class SupabaseAlmacenamientoAdapter implements AlmacenamientoPuerto {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${lms.almacenamiento.bucket}")
    private String bucket;

    @Value("${lms.almacenamiento.url-firmada-minutos:15}")
    private long urlFirmadaMinutos;

    @Override
    public String subir(String rutaDestino, InputStream contenido, long tamanio, String tipoContenido) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(rutaDestino)
                .contentType(tipoContenido)
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(contenido, tamanio));
        return rutaDestino;
    }

    @Override
    public String generarUrlDescarga(String ruta) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(ruta)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(urlFirmadaMinutos))
                .getObjectRequest(getRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    @Override
    public void eliminar(String ruta) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(ruta).build());
    }
}
