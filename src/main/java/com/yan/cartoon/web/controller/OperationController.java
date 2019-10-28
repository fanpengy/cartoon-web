package com.yan.cartoon.web.controller;

import com.yan.cartoon.web.util.HackHH;
import com.yan.cartoon.web.util.OKHttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Controller
@RequestMapping("op")
public class OperationController {

    @Autowired
    private HackHH hackHH;

    @RequestMapping("update")
    @ResponseBody
    public String update() {
        try {
            hackHH.update();
            return "completed";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @RequestMapping("finishchange")
    @ResponseBody
    public String finishChange() {
        try {
            hackHH.finishChange();
            return "completed";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @RequestMapping("proxy")
    @ResponseBody
    public String testProxy(String proxy) throws IOException {
        String[] split = proxy.split(":");
        return new OKHttpUtils().get("https://www.baidu.com", new Proxy(Proxy.Type.HTTP,
                new InetSocketAddress(split[0], Integer.parseInt(split[1])))).string();
    }

}
