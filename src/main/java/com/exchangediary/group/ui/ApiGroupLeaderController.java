package com.exchangediary.group.ui;

import com.exchangediary.group.service.GroupLeaderService;
import com.exchangediary.group.ui.dto.request.GroupKickOutRequest;
import com.exchangediary.group.ui.dto.request.GroupLeaderHandOverRequest;
import com.exchangediary.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups/{groupId}/leader")
@RequiredArgsConstructor
public class ApiGroupLeaderController {
    private final GroupLeaderService groupLeaderService;
    private final NotificationService notificationService;

    @PatchMapping("/hand-over")
    public ResponseEntity<Void> handOverGroupLeader(
            @PathVariable Long groupId,
            @RequestAttribute Long memberId,
            @RequestBody GroupLeaderHandOverRequest request
    ) {
        groupLeaderService.handOverGroupLeader(groupId, memberId, request);
        return ResponseEntity
                .ok()
                .build();
    }

    @PatchMapping("/skip-order")
    public ResponseEntity<Void> skipDiaryOrder(@PathVariable Long groupId) {
        groupLeaderService.skipDiaryOrder(groupId);
        notificationService.pushDiaryOrderNotification(groupId);
        return ResponseEntity
                .ok()
                .build();
    }
    @PatchMapping("/leave")
    public ResponseEntity<Void> kickOutMember(
            @PathVariable Long groupId,
            @RequestBody GroupKickOutRequest request
    ) {
        groupLeaderService.kickOutMember(groupId, request);
        return ResponseEntity
                .ok()
                .build();
    }
}
