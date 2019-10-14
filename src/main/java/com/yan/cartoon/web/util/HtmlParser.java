package com.yan.cartoon.web.util;

import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HtmlParser {
    public static List<Pair<String,String>> parseList(String source) {
        Document doc = Jsoup.parse(source);
        Elements slmsas = doc.select("div[class=slmsa]");
        List<Pair<String,String>> list = new ArrayList<>();
        for (Element slmsa : slmsas) {
            String href = slmsa.select("a").get(0).attr("href");
            String name = slmsa.select("span[class=t]").get(0).html();
            list.add(new Pair<>(name,href));
            System.out.println("name:" + name + " href:" + href);
        }
        return list;
    }

    public static List<Pair<Integer,String>> parseCatalogue(String source) {
        Document doc = Jsoup.parse(source);
        Elements lis = doc.select("li");
        List<Pair<Integer,String>> catalogue = new ArrayList<>();
        for (Element li : lis) {
            Element a = li.select("a").get(0);
            String href = a.attr("href");

            catalogue.add(new Pair<>(Integer.parseInt(href.substring(href.lastIndexOf("=") + 1)),href));

        }
        return catalogue;
    }

    public static String parsePicture(String source) {
        Document doc = Jsoup.parse(source);
        Elements imgs = doc.select("img[class=lazy]");
        if (imgs.size() > 0) {
            String picUrl = imgs.get(0).attr("data-original");

            return picUrl.substring(0,picUrl.lastIndexOf("/")) + "/%s" + picUrl.substring(picUrl.lastIndexOf("."));
        }
        return null;
    }

    public static List<String> parseIP(String source, Function<Document, List<String>> parseIPFunction) {
        List<String> ips = new ArrayList<>();
        Document doc = Jsoup.parse(source);
        return parseIPFunction.apply(doc);
        /*Elements trs = doc.select("tr");
        for (Element tr : trs) {
            Elements tds = tr.select("td");
            if (tds.size() > 0 && tds.get(5).toString().contains("HTTPS")) {
                ips.add(tds.get(1).html() + ":" + tds.get(2).html());
            }
        }
        return ips;*/
    }

    public static List<String> parseIPHi(String source) {
        return parseIP(source,d -> {
            List<String> ips = new ArrayList<>();
            Elements trs = d.select("tr");
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                if (tds.size() > 0 && tds.get(3).html().contains("HTTPS")
                        && Double.parseDouble(tds.get(5).html().replace("s","")) < 1.0) {
                    ips.add(tds.get(0).html() + ":" + tds.get(1).html());
                }
            }
            return ips;
        });

    }

}
