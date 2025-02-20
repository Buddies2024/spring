package com.exchangediary.group.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.ConfilctException;
import com.exchangediary.global.exception.serviceexception.DuplicateException;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GroupValidationService {
    public void checkNumberOfGroupMembers(int numberOfGroupMembers) {
        if (numberOfGroupMembers >= 7) {
            throw new ConfilctException(
                    ErrorCode.FULL_MEMBERS_OF_GROUP,
                    "",
                    String.valueOf(numberOfGroupMembers)
            );
        }
    }

    public void checkNicknameDuplicate(List<GroupMember> groupMembers, String nickname) {
        if (groupMembers.stream()
                .anyMatch(groupMember -> groupMember.getNickname().equals(nickname))) {
            throw new DuplicateException(
                    ErrorCode.NICKNAME_DUPLICATED,
                    "",
                    nickname
            );
        }
    }

    public void checkProfileDuplicate(List<GroupMember> groupGroupMembers, String profileImage) {
        if (groupGroupMembers.stream()
                .anyMatch(groupMember -> groupMember.getProfileImage().equals(profileImage))) {
            throw new DuplicateException(
                    ErrorCode.PROFILE_DUPLICATED,
                    "",
                    profileImage
            );
        }
    }

    public void checkSkipOrderAuthority(Group group) {
        if (group.getLastSkipOrderDate().isEqual(LocalDate.now())) {
            throw new ConfilctException(
                    ErrorCode.ALREADY_SKIP_ORDER_TODAY,
                    "",
                    LocalDate.now().toString()
            );
        }
    }
}
