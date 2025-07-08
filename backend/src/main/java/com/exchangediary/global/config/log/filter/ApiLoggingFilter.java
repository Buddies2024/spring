package com.exchangediary.global.config.log.filter;

import com.exchangediary.global.config.log.wrapper.RequestWrapper;
import com.exchangediary.global.config.log.wrapper.ResponseWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.web.multipart.support.MultipartResolutionDelegate.isMultipartRequest;

@Slf4j
public class ApiLoggingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String httpMethod = httpRequest.getMethod();
        ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);

        if (isMultipartRequest(httpRequest)) {
            String multipartFile = getMultipartRequest(httpRequest);
            log.info("[Request] {} {} \n{}", httpMethod, requestURI, multipartFile);
            chain.doFilter(request, responseWrapper);
        } else {
            RequestWrapper requestWrapper = new RequestWrapper(httpRequest);
            log.info("[Request] {} {} {}", httpMethod, requestURI, requestWrapper.getRequestBody());
            chain.doFilter(requestWrapper, responseWrapper);
        }
        int statusCode = responseWrapper.getStatus();
        log.info("[Response] {} {} {}", statusCode, requestURI, responseWrapper.getResponseBody());
        responseWrapper.copyBodyToResponse();
    }

    private String getMultipartRequest(HttpServletRequest request) {
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest multipart = multipartResolver.resolveMultipart(request);
        Map<String, MultipartFile> multipartFileByName = multipart.getFileMap();

        String multipartLog = "";

        try {
            String jsonData = new String(multipartFileByName.get("data").getBytes(), StandardCharsets.UTF_8);
            multipartLog = String.format("Name: data, Value: %s", jsonData);
        } catch (IOException exception) {
            log.error("{} caused by {}", exception.getMessage(), "data");
        }
        MultipartFile file = multipartFileByName.get("file");
        if (file != null) {
            multipartLog += String.format("\nName: file, Original File Name: %s, Size: %d bytes", file.getOriginalFilename(), file.getSize());
        }
        return multipartLog;
    }

    @Override
    public void destroy() {
    }
}
