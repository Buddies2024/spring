package com.exchangediary.group.ui;

import com.exchangediary.group.service.GroupLeaderService;
import com.exchangediary.group.ui.dto.notification.GroupLeaderHandOverNotification;
import com.exchangediary.group.ui.dto.notification.GroupLeaderKickOutNotification;
import com.exchangediary.group.ui.dto.notification.GroupLeaderSkipDiaryNotification;
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
            @PathVariable String groupId,
            @RequestAttribute Long memberId,
            @RequestBody GroupLeaderHandOverRequest request
    ) {
        GroupLeaderHandOverNotification notification = groupLeaderService.handOverGroupLeader(groupId, memberId, request);

        notificationService.pushToAllGroupMembersExceptMemberAndLeader(
                groupId,
                notification.oldLeaderId(),
                String.format("%s(이)가 새로운 방장이 되었습니다.", notification.newLeaderNickname())
        );
        notificationService.pushNotification(notification.newLeaderId(), "방장이 되었습니다. 방장 권한을 실행해보세요!");

        return ResponseEntity
                .ok()
                .build();
    }

    @PatchMapping("/skip-order")
    public ResponseEntity<Void> skipDiaryOrder(@PathVariable String groupId) {
        GroupLeaderSkipDiaryNotification notification = groupLeaderService.skipDiaryOrder(groupId);

        notificationService.pushNotification(notification.skipDiaryMemberId(), "방장이 일기 순서를 건너뛰었어요.\n다음 순서를 기다려주세요!");
        notificationService.pushDiaryOrderNotification(groupId);

        return ResponseEntity
                .ok()
                .build();
    }

    @PatchMapping("/leave")
    public ResponseEntity<Void> kickOutMember(
            @PathVariable String groupId,
            @RequestBody GroupKickOutRequest request
    ) {
        GroupLeaderKickOutNotification notification = groupLeaderService.kickOutMember(groupId, request);

        notificationService.pushToAllGroupMembersExceptMemberAndLeader(
                groupId,
                notification.kickOutMemberId(),
                String.format("방장이 %s(이)를 그룹에서 내보냈어요!", notification.kickOutMemberNickname())
        );
        notificationService.pushNotification(notification.kickOutMemberId(), "앗, 그룹에서 내보내졌어요.\n다시 스프링을 시작해볼까요?");

        return ResponseEntity
                .ok()
                .build();
    }
}
