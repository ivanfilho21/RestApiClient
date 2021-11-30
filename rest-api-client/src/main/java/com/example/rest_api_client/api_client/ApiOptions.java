package com.example.rest_api_client.api_client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Esta classe possui as configurações para realizar uma chamada à API,
 * como por exemplo, o endpoint, charset, método de requisição (GET, POST, etc),
 * dentre outros.
 *
 * Utilize o Builder para criar uma instância de ApiOptions.
 *
 * @author Ivan Nascimento
 */

public class ApiOptions {
    private final @NonNull Charset charset;
    private @Nullable String endpoint;
    private @Nullable RequestMethod method;
    private @Nullable Map<String, String> queryParams;
    private @Nullable Map<String, String> requestParams;

    private ApiOptions(@NonNull Charset charset) {
        this.charset = charset;
    }

    @NonNull
    public Charset getCharset() {
        return charset;
    }

    public void setEndpoint(@NonNull String endpoint) {
        this.endpoint = endpoint;
    }

    @NonNull
    public RequestMethod getMethod() {
        return method == null ? RequestMethod.GET : method;
    }

    public void setMethod(@NonNull RequestMethod method) {
        this.method = method;
    }

    @Nullable
    public Map<String, String> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(@Nullable Map<String, String> requestParams) {
        this.requestParams = requestParams;
    }

    @Nullable
    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(@NonNull Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    @Nullable
    public String getEndpoint() {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        if (queryParams == null) {
            return endpoint;
        }

        result.append(endpoint);

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey() == null ? "" : entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue();

            if (key.isEmpty()) {
                continue;
            }

            if (first) {
                first = false;
                result.append("?");
            } else
                result.append("&");

            try {
                result.append(URLEncoder.encode(key, charset.displayName()));
                result.append("=");
                result.append(URLEncoder.encode(value, charset.displayName()));
            } catch (UnsupportedEncodingException e) {
                // Empty
            }
        }

        return result.toString();
    }

    @NonNull
    public String getRequestParamsString() {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        if (requestParams == null) {
            return result.toString();
        }

        for (Map.Entry<String, String> pair : requestParams.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            try {
                result.append(URLEncoder.encode(pair.getKey(), charset.displayName()));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), charset.displayName()));
            } catch (UnsupportedEncodingException e) {
                // Empty
            }
        }

        return result.toString();
    }

    public static class Builder {
        private final ApiOptions apiOptions;

        public Builder(@NonNull Charset charset) {
            apiOptions = new ApiOptions(charset);
        }

        public Builder setEndpoint(String endpoint) {
            apiOptions.endpoint = endpoint;
            return this;
        }

        public Builder setRequestMethod(RequestMethod method) {
            apiOptions.method = method;
            return this;
        }

        public Builder withQueryParams(Map<String, String> params) {
            apiOptions.queryParams = params;
            return this;
        }

        public Builder withRequestParams(Map<String, String> params) {
            apiOptions.requestParams = params;
            return this;
        }

        public ApiOptions build() {
            return apiOptions;
        }
    }
}
