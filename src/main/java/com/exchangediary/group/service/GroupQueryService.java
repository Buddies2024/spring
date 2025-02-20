package com.exchangediary.group.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.group.domain.GroupRepository;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.ui.dto.response.GroupNicknameVerifyResponse;
import com.exchangediary.group.ui.dto.response.GroupProfileResponse;
import com.exchangediary.group.ui.dto.response.GroupMonthlyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupQueryService {
    private final GroupValidationService groupValidationService;
    private final GroupRepository groupRepository;

    public Group findGroup(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.GROUP_NOT_FOUND,
                        "",
                        groupId
                ));
    }

    public String verifyCode(String code) {
        Group group = groupRepository.findById(code)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.GROUP_NOT_FOUND,
                        "그룹코드가 유효하지 않습니다.",
                        code
                ));
        return group.getId();
    }

    public GroupProfileResponse viewSelectableProfileImage(String groupId) {
        Group group = findGroup(groupId);
        List<GroupMember> groupMembers = group.getGroupMembers();
        return GroupProfileResponse.from(groupMembers);
    }

    public GroupNicknameVerifyResponse verifyNickname(String groupId, String nickname) {
        Group group = findGroup(groupId);
        groupValidationService.checkNicknameDuplicate(group.getGroupMembers(), nickname);
        return GroupNicknameVerifyResponse.from(true);
    }

    public GroupMonthlyResponse getGroupMonthlyInfo(String groupId) {
        Group group = findGroup(groupId);
        return GroupMonthlyResponse.of(group);
    }
}
