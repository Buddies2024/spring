package com.exchangediary.group.ui.dto.response;

import com.exchangediary.group.domain.entity.Group;
import lombok.Builder;

@Builder
public record GroupMonthlyResponse(
        String id,
        String name,
        int createdYear,
        int createdMonth,
        String code
) {
    public static GroupMonthlyResponse from(Group group) {
        return GroupMonthlyResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .createdYear(group.getCreatedAt().getYear())
                .createdMonth(group.getCreatedAt().getMonthValue())
                .build();
    }
}
