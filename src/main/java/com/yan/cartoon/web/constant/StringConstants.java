package com.yan.cartoon.web.constant;

public interface StringConstants {

    String htmlPrefix0 = "<!DOCTYPE html>\n" +
            "<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\"\n" +
            "         pageEncoding=\"UTF-8\"%>\n" +
            "<html lang=\"en\" style=\"font-size: 160px;\">\n" +
            "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
            "\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"/>\n" +
            "    <meta name=\"apple-mobile-web-app-capable\" content=\"yes\"/>\n" +
            "    <meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\"/>\n" +
            "    <meta name=\"format-detection\" content=\"telephone=no, email=no\"/>\n" +
            "    <title>";

    String htmlPrefixweb1 = "</title>\n" +
            "    <link rel=\"stylesheet\" href=\"/css/base.css\"/>\n" +
            "    <link rel=\"stylesheet\" href=\"/css/landing.css\"/>\n" +
            "    <style>\n" +
            "            .txt p{\n" +
            "                font-size: 18px;\n" +
            "                line-height: 35px;\n" +
            "            }\n" +
            "            .article-info {\n" +
            "                padding: 10px 14px;\n" +
            "                font-size: 14px;\n" +
            "            }\n" +
            "            .article-title {\n" +
            "                padding: 19px 666px 2px;\n" +
            "                font-size: 24px;\n" +
            "                line-height: 30px;\n" +
            "            }\n" +
            "            .article-info .article-date {\n" +
            "                float: left;\n" +
            "            }\n" +
            "            .article-info .article-pviews {\n" +
            "                float: right;\n" +
            "            }\n" +
            "\n" +
            "\n" +
            "            .content img{\n" +
            "                text-align: center;font-weight: 900;font-size: 20px;\n" +
            "            }\n" +
            "            .content p {\n" +
            "                text-indent: 0rem;\n" +
            "            }\n" +
            "            img{\n" +
            "                max-width: 100%;\n" +
            "                min-width:100%;\n" +
            "            }\n" +
            "            .landing .main .txt p {\n" +
            "                text-indent: 0rem;\n" +
            "            }\n" +
            "            .landing .main {\n" +
            "                height: auto;\n" +
            "                overflow: hidden;\n" +
            "                margin-top: 0.8rem;\n" +
            "                text-align: left;\n" +
            "            }\n" +
            "            .txt p {\n" +
            "                line-height: 0;\n" +
            "            }\n" +
            "    </style>\n" +
            "    <style>\n" +
            "        @page {\n" +
            "            margin: 0;\n" +
            "            size: auto;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "\n" +
            "\n" +
            "<div class=\"main\">\n" +
            "<div class=\"txt\">\n" +
            "\t<div class=\"content\">";

    String htmlPostfix = "</div>\n" +
            "</div>\n" +
            "</div>\n" +
            "</html>";

    String pageEncoding = "<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\"\n" +
            "         pageEncoding=\"UTF-8\"%>\n";
    String jumpUrl = "<p style=\"font-size:%spx\"> <a href=\"/api/get?page=%s\">%s</a></p>\n";

    String jumpSpanUrl = "<span style=\"font-size:%spx\"> <a href=\"/api/get?page=%s\">%s</a></span>\n";

    String pictureLine = "            <p style=\"white-space: normal;\"><img style=\"min-height: 200px; display: inline;\" src=\"%s\"></p>\n";

    String BlankString = "";
}
