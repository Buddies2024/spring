package com.exchangediary.group.ui.dto.request;

import jakarta.validation.constraints.NotNull;

public record GroupLeaderHandOverRequest(
        @NotNull Integer nextLeaderIndex
){
}
