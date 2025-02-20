package com.exchangediary.group.ui;

import com.exchangediary.group.service.GroupLeaveService;
import com.exchangediary.group.service.GroupMemberQueryService;
import com.exchangediary.group.service.GroupQueryService;
import com.exchangediary.group.service.GroupCreateJoinService;
import com.exchangediary.group.ui.dto.request.GroupCodeRequest;
import com.exchangediary.group.ui.dto.request.GroupJoinRequest;
import com.exchangediary.group.ui.dto.request.GroupCreateRequest;
import com.exchangediary.group.ui.dto.request.GroupNicknameRequest;
import com.exchangediary.group.ui.dto.response.GroupCreateResponse;
import com.exchangediary.group.ui.dto.response.GroupIdResponse;
import com.exchangediary.group.ui.dto.response.GroupMembersResponse;
import com.exchangediary.group.ui.dto.response.GroupNicknameVerifyResponse;
import com.exchangediary.group.ui.dto.response.GroupProfileResponse;
import com.exchangediary.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class ApiGroupController {
    private final GroupCreateJoinService groupCreateJoinService;
    private final GroupQueryService groupQueryService;
    private final GroupLeaveService groupLeaveService;
    private final GroupMemberQueryService groupMemberQueryService;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<GroupCreateResponse> createGroup(
            @RequestBody @Valid GroupCreateRequest request,
            @RequestAttribute Long memberId
    ) {
        GroupCreateResponse response = groupCreateJoinService.createGroup(request, memberId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/code/verify")
    public ResponseEntity<GroupIdResponse> verifyGroupCode(
            @RequestBody @Valid GroupCodeRequest request
    ) {
        String groupId = groupQueryService.verifyCode(request.code());
        GroupIdResponse response = GroupIdResponse.builder()
                .groupId(groupId)
                .build();
        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/{groupId}/profile-image")
    public ResponseEntity<GroupProfileResponse> viewSelectableProfileImage(
            @PathVariable String groupId) {
        GroupProfileResponse groupProfileResponse = groupQueryService.viewSelectableProfileImage(groupId);
        return ResponseEntity
                .ok()
                .body(groupProfileResponse);
    }

    @GetMapping("/{groupId}/nickname/verify")
    public ResponseEntity<GroupNicknameVerifyResponse> verifyNickname(
            @PathVariable String groupId,
            @ModelAttribute @Valid GroupNicknameRequest request
    ) {
        GroupNicknameVerifyResponse response =
                groupQueryService.verifyNickname(groupId, request.nickname());
        return ResponseEntity
                .ok()
                .body(response);
    }

    @PatchMapping("/{groupId}/join")
    public ResponseEntity<Void> joinGroup(
            @PathVariable String groupId,
            @RequestAttribute Long memberId,
            @RequestBody @Valid GroupJoinRequest request
            ) {
        groupCreateJoinService.joinGroup(groupId, memberId, request);
        notificationService.pushToAllGroupMembersExceptMember(groupId, memberId, "새로운 친구가 들어왔어요!");
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<GroupMembersResponse> listGroupMembersInfo(
            @PathVariable String groupId,
            @RequestAttribute Long memberId
    ) {
        GroupMembersResponse response = groupMemberQueryService.listGroupMembersInfo(memberId, groupId);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @PatchMapping("{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable String groupId,
            @RequestAttribute Long memberId
    ) {
        groupLeaveService.leaveGroup(groupId, memberId);
        notificationService.pushToAllGroupMembersExceptMember(groupId, memberId, "친구가 그룹에서 나갔어요!");
        return ResponseEntity
                .ok()
                .build();
    }
}
