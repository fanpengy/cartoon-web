package com.yan.cartoon.web.constant;

import java.io.File;
import java.net.URL;

public class ObjectConstants {

    public static File AES_COMPUTE_FILE = getFile("files/public.js");

    private static File getFile(String fileName) {
        URL url = ObjectConstants.class.getClassLoader().getResource(fileName);
        return new File(url == null ? "" : url.getFile());
    }


}
