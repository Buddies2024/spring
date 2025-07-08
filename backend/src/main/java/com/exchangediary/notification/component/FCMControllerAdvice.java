package com.exchangediary.notification.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class FCMControllerAdvice {
    @Value("${fcm.api-key}")
    private String apiKey;
    @Value("${fcm.auth-domain}")
    private String authDomain;
    @Value("${fcm.project-id}")
    private String projectId;
    @Value("${fcm.storage-bucket}")
    private String storageBucket;
    @Value("${fcm.messaging-sender-id}")
    private String messagingSenderId;
    @Value("${fcm.app-id}")
    private String appId;
    @Value("${fcm.measurement-id}")
    private String measurementId;
    @Value("${fcm.vapid-key}")
    private String vapidKey;

    @ModelAttribute
    public void handleRequest(Model model) {
        model.addAttribute("apiKey", apiKey);
        model.addAttribute("authDomain", authDomain);
        model.addAttribute("projectId", projectId);
        model.addAttribute("storageBucket", storageBucket);
        model.addAttribute("messagingSenderId", messagingSenderId);
        model.addAttribute("appId", appId);
        model.addAttribute("measurementId", measurementId);
        model.addAttribute("vapidKey", vapidKey);
    }
}
