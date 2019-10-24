package com.yan.cartoon.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.regex.Pattern;

@Controller
@RequestMapping("api")
public class HomeController {

    @RequestMapping("get")
    public String get(String page) {
        if (page.equals("homepage")) {
            return page;
        }
        String chapterId = Pattern.compile("[^0-9]").matcher(page).replaceAll("");
        String folder = page.substring(0,page.lastIndexOf(chapterId));
        return String.format("%s/%s",folder,page);
    }



}
