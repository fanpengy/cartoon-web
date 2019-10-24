package com.yan.cartoon.web.util;

import okhttp3.Response;
import okhttp3.internal.Util;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class HttpResult {

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
        return new String(this.bytes, this.charset.name());
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
