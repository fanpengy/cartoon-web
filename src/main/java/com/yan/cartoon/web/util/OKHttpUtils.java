package com.yan.cartoon.web.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.util.concurrent.TimeUnit;

public class OKHttpUtils {

    private static final int CONNECTION_TIMEOUT = 3000;

    private static final int READ_TIMEOUT = 10000;


    private OkHttpClient client;

    public OKHttpUtils() {
        this(ProxySelector.getDefault());
    }

    public OKHttpUtils(ProxySelector proxySelector) {
        this.client = new OkHttpClient.Builder().
                connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS).
                readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS).
                retryOnConnectionFailure(false).
                proxySelector(proxySelector).
                build();
    }

    public HttpResult get(String url) throws IOException {
        return get(url, null);
    }

    public HttpResult get(String url, Proxy proxy) throws IOException {

        Request request = new Request.Builder().get().url(url).build();
        OkHttpClient newClient = buildClient(proxy);
        try (Response response = newClient.newCall(request).execute()) {
            return new HttpResult(response);
        }
    }


    /**
     *
     * @param url
     * @param requestBody
     * @throws IOException
     */
    public HttpResult post(String url, RequestBody requestBody) throws IOException {
        return post(url, requestBody, null);
    }

    public HttpResult post(String url, RequestBody requestBody,
                           Proxy proxy) throws IOException {
        Request request = new Request.Builder().
                post(requestBody == null ? RequestBody.create(null,"") : requestBody).
                url(url).build();

        OkHttpClient newClient = buildClient(proxy);
        try (Response response = newClient.newCall(request).execute()) {
            return new HttpResult(response);
        }
    }

    private OkHttpClient buildClient(Proxy proxy) {
        OkHttpClient.Builder builder = client.newBuilder();

        if (proxy != null) {
            builder.proxySelector(ProxySelector.getDefault()).
                    proxy(proxy);
        }
        return builder.build();
    }



}
