package com.exchangediary.notification.ui;

import com.exchangediary.notification.service.NotificationTokenService;
import com.exchangediary.notification.ui.dto.request.NotificationTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class ApiNotificationController {
    private final NotificationTokenService notificationTokenService;

    @PatchMapping("/notifications/token")
    public ResponseEntity<Void> saveNotification(
            @RequestBody NotificationTokenRequest notificationTokenRequest,
            @RequestAttribute Long memberId
    ) {
        notificationTokenService.saveNotificationToken(notificationTokenRequest, memberId);
        return ResponseEntity
                .ok()
                .build();
    }
}
