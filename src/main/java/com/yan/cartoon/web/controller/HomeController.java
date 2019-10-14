package com.yan.cartoon.web.controller;

import com.yan.cartoon.web.util.HackHH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.regex.Pattern;

@Controller
@RequestMapping("api")
public class HomeController {

    @Autowired
    private HackHH hackHH;


    @RequestMapping("get")
    public String get(String page) {
        if (page.equals("homepage")) {
            return page;
        }
        String chapterId = Pattern.compile("[^0-9]").matcher(page).replaceAll("");
        String folder = page.substring(0,page.lastIndexOf(chapterId));
        return String.format("%s/%s",folder,page);
    }

    @RequestMapping("update")
    public void update() throws IOException {
        hackHH.update();
    }

    @RequestMapping("finishchange")
    public void finishChange() throws IOException {
        hackHH.finishChange();
    }

}
