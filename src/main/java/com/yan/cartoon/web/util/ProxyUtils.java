package com.yan.cartoon.web.util;

import org.junit.Test;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class ProxyUtils {

    private static final int MIN_PROXY_NUMBER = 2;

    private static final List<Proxy> proxys = new ArrayList<>();

    private static final Map<Proxy, Integer> proxyBlackmap = new HashMap<>();

    private static final Random random = new Random();

    private static final OKHttpUtils httpUtils = new OKHttpUtils();

    public static final ProxySelector proxySelector = new ProxySelector() {
        @Override
        public List<Proxy> select(URI uri) {
            return Collections.singletonList(getProxy());
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            clearProxy(new Proxy(Proxy.Type.HTTP, sa));
        }
    };

    public static Proxy getProxy() {
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

    public static synchronized void clearProxy(Proxy proxy) {
        proxys.remove(proxy);
        proxyBlackmap.put(proxy, proxyBlackmap.getOrDefault(proxy, 0) + 1);
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
            List<String> ips = getFromwww_xsdaili_com();
            for (String ip : ips) {
                try {
                    String[] proxy = ip.split(":");
                    proxys.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy[0], Integer.parseInt(proxy[1]))));
                } catch (Exception e) {
                    System.out.print("");
                }
            }
        } catch (Exception e) {
            System.out.println("补充代理失败");
        }
    }

    //region 这里是使用过的各种免费ip网站
    private static List<String> getFromwww_iphai_com() {
        try {
            String html = httpUtils.get("http://www.iphai.com/free/ng").string();
            return HtmlParser.parseIPHi(html);
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }


    private static List<String> getFromwww_xsdaili_com() {
        try {
            String html = httpUtils.get("http://www.xsdaili.com/index.php?s=/index/index.html").string();
            String url = HtmlParser.xsdailiStep1(html);
            html = httpUtils.get("http://www.xsdaili.com" + url).string();
            return HtmlParser.xsdailiStep2(html);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    //end region

    @Test
    public void test() {
        getFromwww_xsdaili_com();
    }
}
