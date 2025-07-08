package com.exchangediary.global.ui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReactRedirectController {
    
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${react.url}")
    private String reactUrl;

    @GetMapping("/react/**")
    public String redirectToReact(HttpServletRequest request) {
        String host = request.getServerName();
        String path = request.getRequestURI();

        if ("local".equals(activeProfile)) {
            return "redirect:http://" + host + ":3000" + path;
        }

        return "redirect:" + reactUrl + path;
    }
}
