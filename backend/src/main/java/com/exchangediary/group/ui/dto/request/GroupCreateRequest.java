package com.exchangediary.group.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GroupCreateRequest(
        @NotBlank String groupName,
        @NotBlank String profileImage,
        @NotBlank String nickname
) {
}
