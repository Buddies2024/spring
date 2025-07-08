package com.exchangediary.group.ui.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record GroupJoinRequest(
        @NotEmpty String profileImage,
        @NotEmpty String nickname
) {
}
