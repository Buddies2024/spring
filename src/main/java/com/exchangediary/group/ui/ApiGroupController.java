package com.exchangediary.group.ui;

import com.exchangediary.group.service.GroupCodeService;
import com.exchangediary.group.service.GroupJoinService;
import com.exchangediary.group.service.GroupLeaveService;
import com.exchangediary.group.service.GroupQueryService;
import com.exchangediary.group.service.GroupCreateService;
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
    private final GroupCreateService groupCreateService;
    private final GroupJoinService groupJoinService;
    private final GroupCodeService groupCodeService;
    private final GroupQueryService groupQueryService;
    private final GroupLeaveService groupLeaveService;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<GroupCreateResponse> createGroup(
            @RequestBody @Valid GroupCreateRequest request,
            @RequestAttribute Long memberId
    ) {
        GroupCreateResponse response = groupCreateService.createGroup(request, memberId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/code/verify")
    public ResponseEntity<GroupIdResponse> verifyGroupCode(
            @RequestBody @Valid GroupCodeRequest request
    ) {
        Long groupId = groupCodeService.verifyCode(request.code());
        GroupIdResponse response = GroupIdResponse.builder()
                .groupId(groupId)
                .build();
        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/{groupId}/profile-image")
    public ResponseEntity<GroupProfileResponse> viewSelectableProfileImage(
            @PathVariable Long groupId) {
        GroupProfileResponse groupProfileResponse = groupQueryService.viewSelectableProfileImage(groupId);
        return ResponseEntity
                .ok()
                .body(groupProfileResponse);
    }

    @GetMapping("/{groupId}/nickname/verify")
    public ResponseEntity<GroupNicknameVerifyResponse> verifyNickname(
            @PathVariable Long groupId,
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
            @PathVariable Long groupId,
            @RequestBody @Valid GroupJoinRequest request,
            @RequestAttribute Long memberId
    ) {
        groupJoinService.joinGroup(groupId, request, memberId);
        notificationService.pushToAllGroupMembers(groupId, "새로운 친구가 들어왔어요!");
        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<GroupMembersResponse> listGroupMembersInformation(
            @PathVariable Long groupId,
            @RequestAttribute Long memberId
    ) {
        GroupMembersResponse response = groupQueryService.listGroupMembersInformation(memberId, groupId);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @PatchMapping("{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable Long groupId,
            @RequestAttribute Long memberId
    ) {
        groupLeaveService.leaveGroup(groupId, memberId);
        notificationService.pushToAllGroupMembersExceptMember(groupId, memberId, "친구가 그룹에서 나갔어요!");
        return ResponseEntity
                .ok()
                .build();
    }
}
