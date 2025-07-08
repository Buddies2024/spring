package com.exchangediary.global.config.log.wrapper;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseWrapper extends ContentCachingResponseWrapper {

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public String getResponseBody() throws IOException {
        return new String(getContentAsByteArray(), StandardCharsets.UTF_8);
    }
}
