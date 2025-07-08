package com.exchangediary.global.config.log.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RequestWrapper extends HttpServletRequestWrapper {
    private final byte[] cachedInputStream;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.cachedInputStream = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            private final InputStream cachedBodyInputStream = new ByteArrayInputStream(cachedInputStream);

            @Override
            public int read() throws IOException {
                return cachedBodyInputStream.read();
            }

            @Override
            public boolean isFinished() {
                try {
                    return cachedBodyInputStream.available() == 0;
                } catch (IOException ignored) {}
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }

    public String getRequestBody() throws IOException {
        return new String(cachedInputStream, StandardCharsets.UTF_8);
    }
}
