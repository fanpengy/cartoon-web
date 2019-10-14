package com.yan.cartoon.web.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class OKHttpUtils {

    private static final int CONNECTION_TIMEOUT = 3000;

    private static final int READ_TIMEOUT = 10000;

    private static final int MIN_PROXY_NUMBER = 2;

    private static final List<Proxy> proxys = new ArrayList<>();

    private static final Map<Proxy,Integer> proxyBlackmap = new HashMap<>();

    private static final Random random = new Random();

    private static final ProxySelector proxySelector = new ProxySelector() {
        @Override
        public List<Proxy> select(URI uri) {
            return Collections.singletonList(getProxy());
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            clearProxy(new Proxy(Proxy.Type.HTTP,sa));
        }
    };

    private static final OkHttpClient client = new OkHttpClient.Builder().
            connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS).
            readTimeout(READ_TIMEOUT,TimeUnit.MILLISECONDS).
            retryOnConnectionFailure(false).
            proxySelector(proxySelector).
            build();

    public static HttpResult get(String url, boolean... alt) throws IOException {
        return get(url,null,0,alt);
    }

    /**
     *
     * @param url
     * @param proxy
     * @param port
     * @param alt 可选参数，alt[0]表示是否使用默认代理，如果参数proxy和参数port可用则忽略
     * @return
     * @throws IOException
     */
    public static HttpResult get(String url, String proxy, int port, boolean... alt) throws IOException {

        Request request = new Request.Builder().get().url(url).build();
        OkHttpClient newClient = buildClient(proxy, port, alt);
        try (Response response = newClient.newCall(request).execute()) {
            return new HttpResult(response);
        }
    }


    /**
     *
     * @param url
     * @param requestBody
     * @param alt
     * @return 可选参数，alt[0]表示是否使用默认代理，如果参数proxy和参数port可用则忽略
     * @throws IOException
     */
    public static HttpResult post(String url, RequestBody requestBody, boolean... alt) throws IOException {
        return post(url,requestBody,null,0,alt);
    }

    public static HttpResult post(String url, RequestBody requestBody,
                                  String proxy, int port,boolean... alt) throws IOException {
        Request request = new Request.Builder().
                post(requestBody == null ? RequestBody.create(null,"") : requestBody).
                url(url).build();

        OkHttpClient newClient = buildClient(proxy, port, alt);
        try (Response response = newClient.newCall(request).execute()) {
            return new HttpResult(response);
        }
    }

    private static OkHttpClient buildClient(String proxy, int port,boolean... alt) {
        OkHttpClient.Builder builder = client.newBuilder();
        if (alt.length == 0 || !alt[0]) {
            builder.proxySelector(ProxySelector.getDefault());
        }
        if (proxy != null && port > 0 && port <= 65535) {
            builder.proxySelector(ProxySelector.getDefault()).
                    proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxy,port)));
        }
        return builder.build();
    }

    static class HttpResult {

        private int code;

        private byte[] bytes;

        private Charset charset;

        public HttpResult(Response response) throws IOException {
            this.code = response.code();
            this.bytes = response.body().bytes();
            this.charset = response.body().contentType() == null ?
                    response.body().contentType().charset(Util.UTF_8) : Util.UTF_8;
        }

        public byte[] bytes() throws IOException {
            this.checkCode();
            return this.bytes;
        }


        public String string() throws IOException {
            this.checkCode();
            return new String(this.bytes,this.charset.name());
        }

        public InputStream inputStream() throws IOException {
            this.checkCode();
            return new ByteArrayInputStream(this.bytes);
        }

        private void checkCode() throws IOException {
            if (this.code == HttpStatus.NOT_FOUND.value()) {
                throw new FileNotFoundException("404");
            }
            if (this.code != HttpStatus.OK.value()) {
                throw new IOException(code + "");
            }
        }
    }

    private static Proxy getProxy() {
        if (proxys.size() == 0) {
            synchronized (proxys) {
                if (proxys.size() == 0) {
                    initProxys();
                }
            }
        }

        if (proxys.size() > 0) {
            int i = random.nextInt(proxys.size());
            try {
                return proxys.get(i);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("获取代理失败，越界");
                return Proxy.NO_PROXY;
            }
        }
        System.out.println("获取代理失败，没有代理");
        return Proxy.NO_PROXY;
    }

    private static synchronized void clearProxy(Proxy proxy) {
        proxys.remove(proxy);
        proxyBlackmap.put(proxy,proxyBlackmap.getOrDefault(proxy,0) + 1);
        if (proxys.size() <= MIN_PROXY_NUMBER) {
            proxyBlackmap.size();
            proxyBlackmap.entrySet().stream().
                    sorted(Comparator.comparing(Map.Entry::getValue)).
                    map(Map.Entry::getKey).
                    limit(proxyBlackmap.size() / 2).
                    forEach(p -> {
                        if (!proxys.contains(p)) {
                            proxys.add(p);
                        }
                    });

        }
    }

    private static void initProxys() {
        try {
            String html = get("http://www.iphai.com/free/ng", false).string();
            List<String> ips = HtmlParser.parseIPHi(html);
            for (String ip : ips) {
                try {
                    String[] proxy = ip.split(":");
                    proxys.add(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxy[0],Integer.parseInt(proxy[1]))));
                } catch (Exception e){
                    System.out.print("");
                }
            }
        } catch (Exception e) {
            System.out.println("补充代理失败");
        }
    }


}
