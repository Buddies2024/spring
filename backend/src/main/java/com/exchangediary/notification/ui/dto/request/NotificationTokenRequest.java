package com.exchangediary.notification.ui.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record NotificationTokenRequest(
        @NotEmpty String token
) {
}
