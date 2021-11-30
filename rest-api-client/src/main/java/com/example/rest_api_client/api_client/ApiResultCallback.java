package com.example.rest_api_client.api_client;

import androidx.annotation.NonNull;

/**
 * Callback utilizado em caso de Sucesso ou de erro na comunicação.
 *
 * Tenha em mente que o parâmetro response é exatamente a resposta
 * que vem do serviço Web, podendo ser JSON, XML ou qualquer outro
 * tipo de objeto.
 *
 * @author Ivan Nascimento
 */
public interface ApiResultCallback {
    void onSuccess(@NonNull String response);
    void onError(int code, @NonNull String response);
}
