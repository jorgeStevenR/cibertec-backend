package com.cibertec.lms.web.dtos;

import com.cibertec.lms.dominio.curso.Lienzo;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public record LienzoContenidoResponseDTO(int ancho, int alto, JsonNode elementos) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static LienzoContenidoResponseDTO desde(Lienzo lienzo) {
        JsonNode elementos = OBJECT_MAPPER.readTree(lienzo.getElementosJson());
        return new LienzoContenidoResponseDTO(lienzo.getAncho(), lienzo.getAlto(), elementos);
    }
}
