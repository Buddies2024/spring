package com.exchangediary.notification.component;

import com.exchangediary.notification.service.NotificationService;
import com.exchangediary.notification.service.NotificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final NotificationService notificationService;
    private final NotificationTokenService notificationTokenService;

    @Scheduled(cron = "0 0 9,21 * * *")
    public void pushWriteDiaryNotification() {
        notificationService.pushWriteDiaryNotification();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void manageTokenFreshness() {
        notificationTokenService.deleteOldTokens();
    }
}
