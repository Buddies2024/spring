package com.exchangediary.group.ui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GroupLeaderHandOverRequest(
        @NotBlank String nickname
){
}
