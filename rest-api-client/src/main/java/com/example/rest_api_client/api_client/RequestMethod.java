package com.example.rest_api_client.api_client;

/**
 * Um simples Enum com as possibilidades de métodos de requisição suportados no momento.
 *
 * @author Ivan Nascimento
 */
public enum RequestMethod {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

    private final String method;

    RequestMethod(String method) {
        this.method = method;
    }

    public String getValue() {
        return method;
    }
}
