package com.exchangediary.member.ui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReactRedirectController {
    
    @Value("${react.url}")
    private String reactUrl;

    @RequestMapping("/react/**")
    public String redirectToReact(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "redirect:" + reactUrl + path;
    }
}
