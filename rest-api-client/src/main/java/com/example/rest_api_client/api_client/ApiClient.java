package com.example.rest_api_client.api_client;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Uma classe para consumir APIs.
 *
 * @author Ivan Nascimento
 */
public class ApiClient {
    private final ExecutorService executor;
    private final Charset charset;

    public ApiClient(@NonNull Charset charset) {
        executor = Executors.newSingleThreadExecutor();
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    /**
     * Realiza uma comunicação com um serviço da Web.
     * Após receber uma resposta, aciona o @param callback.
     *
     * @param options as opções da requisição, como a url e o tipo de requisição.
     * @param callback será acionado após obter uma resposta do serviço.
     */
    public void request(ApiOptions options, ApiResultCallback callback) {
        executor.execute(() -> {
            URL url = null;

            try {
                url = new URL(options.getEndpoint());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (url != null) {
                HttpURLConnection connection = null;

                Log.d(getClass().getSimpleName(), options.getMethod().getValue() + " " + url);

                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                RequestMethod method = options.getMethod();

                if (connection != null) {
                    try {
                        connection.setRequestMethod(method.getValue());
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    }

                    configureRequest(connection, options);
                    handleResponse(connection, callback);
                    connection.disconnect();
                    return;
                }
            }

            throw new IllegalArgumentException("Ocorreu um erro de conexão. Verifique as configurações da chamada.");
        });
    }

    private void configureRequest(@NonNull HttpURLConnection connection, @NonNull ApiOptions options) {
        if (options.getMethod() == RequestMethod.POST || options.getMethod() == RequestMethod.PUT) {
            connection.setDoInput(true);
            connection.setDoOutput(true);

            try {
                putRequestParams(connection, options.getRequestParamsString());
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "", e);
            }
        }
    }

    private void putRequestParams(@NonNull HttpURLConnection connection, @NonNull String requestParams) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, charset));
        writer.write(requestParams);
        writer.flush();
        writer.close();
        outputStream.close();
    }

    private void handleResponse(@NonNull HttpURLConnection connection, ApiResultCallback callback) {
        int responseCode;
        String response = "";
        String logTitle = "Success";

        try {
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "", e);
            responseCode = -1;
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            response = parseResponse(connection);
            callback.onSuccess(response);
        } else if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            callback.onSuccess(response);
        } else {
            logTitle = "Error";
            response = parseResponse(connection, true);
            callback.onError(responseCode, response);
        }

        Log.d(getClass().getSimpleName(), logTitle + "Response: [" + responseCode + "]");
        Log.d(getClass().getSimpleName(), response);
    }

    @NonNull
    private String parseResponse(@NonNull HttpURLConnection connection) {
        return parseResponse(connection, false);
    }

    @NonNull
    private String parseResponse(@NonNull HttpURLConnection connection, boolean isError) {
        InputStream responseBody = null;

        try {
            responseBody = isError ? connection.getErrorStream() : connection.getInputStream();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "", e);
        }

        if (responseBody != null) {
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, charset);
            BufferedReader br = new BufferedReader(responseBodyReader);
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "", e);
            }

            return sb.toString();
        }

        return "";
    }
}
