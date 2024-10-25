package com.exchangediary.member.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.ForbiddenException;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.member.domain.dto.GroupId;
import com.exchangediary.member.domain.MemberRepository;
import com.exchangediary.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {
    private final MemberRepository memberRepository;

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        "",
                        String.valueOf(memberId)
                ));
    }

    public Optional<Long> findGroupBelongTo(Long memberId) {
        return memberRepository.findGroupIdById(memberId)
                .map(GroupId::groupId);
    }

    public LocalDate getLastViewableDiaryDate(Long memberId) {
        return memberRepository.findLastViewableDiaryDateById(memberId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        "",
                        String.valueOf(memberId)
                ));
    }

    public boolean isMemberinGroup(Long memberId, Long groupId) {
        Optional<GroupId> optionalGroupId = memberRepository.findGroupIdById(memberId);
        if (optionalGroupId.isEmpty() || !optionalGroupId.get().groupId().equals(groupId)) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND, "", "");
        }
        return true;
    }
}
