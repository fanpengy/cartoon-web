package com.yan.cartoon.web.util;

import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import static com.yan.cartoon.web.constant.StringConstants.BlankString;

@Component
public class ScriptCaller {

    private ScriptEngineManager manager = new ScriptEngineManager();

    private ScriptEngine engine = manager.getEngineByName("javascript");

    private Set<File> fileSet = new HashSet<>();


    //可以考虑不抛出异常
    public String call(File file, String name, Object... params) {
        try {
            if (!fileSet.contains(file)) {
                FileReader reader = new FileReader(file);   // 执行指定脚本
                engine.eval(reader);
                reader.close();
                fileSet.add(file);
            }

            Invocable invoke = (Invocable) engine;
            Object o = invoke.invokeFunction(name, params);
            return o.toString();

        } catch (Exception e) {
            // TODO: 2019/10/14 记录日志
            return BlankString;
        }

    }

}
