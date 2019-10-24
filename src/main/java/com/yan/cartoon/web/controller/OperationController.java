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
    public void update() throws IOException {
        hackHH.update();
    }

    @RequestMapping("finishchange")
    public void finishChange() throws IOException {
        hackHH.finishChange();
    }

    @RequestMapping("proxy")
    @ResponseBody
    public String testProxy(String proxy) throws IOException {
        String[] split = proxy.split(":");
        return new OKHttpUtils().get("https://www.baidu.com", new Proxy(Proxy.Type.HTTP,
                new InetSocketAddress(split[0], Integer.parseInt(split[1])))).string();
    }

}
