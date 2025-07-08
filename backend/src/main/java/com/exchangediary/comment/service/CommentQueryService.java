package com.exchangediary.comment.service;

import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.ui.dto.response.CommentCreationVerifyResponse;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.service.DiaryAuthorizationService;
import com.exchangediary.diary.service.DiaryQueryService;
import com.exchangediary.comment.ui.dto.response.CommentResponse;
import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.NotFoundException;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.service.GroupMemberQueryService;
import com.exchangediary.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {
    private final CommentRepository commentRepository;
    private final MemberQueryService memberQueryService;
    private final DiaryQueryService diaryQueryService;
    private final DiaryAuthorizationService diaryAuthorizationService;
    private final CommentAuthorizationService commentAuthorizationService;
    private final GroupMemberQueryService groupMemberQueryService;

    public Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                    .orElseThrow(() -> new NotFoundException(
                        ErrorCode.COMMENT_NOT_FOUND,
                        "",
                        String.valueOf(commentId)
                ));
    }

    public CommentResponse viewComment(Long diaryId, Long memberId, Long commentId) {
        GroupMember groupMember = groupMemberQueryService.findGroupMemberByMemberId(memberId);
        Diary diary = diaryQueryService.findDiary(diaryId);

        diaryAuthorizationService.checkDiaryViewable(groupMember.getLastViewableDiaryDate(), diary);
        Comment comment = findComment(commentId);
        return CommentResponse.of(comment, groupMember.getProfileImage());
    }

    public CommentCreationVerifyResponse verifyCommentCreation(Long diaryId, Long memberId) {
        GroupMember groupMember = groupMemberQueryService.findGroupMemberByMemberId(memberId);
        Diary diary = diaryQueryService.findDiary(diaryId);

        diaryAuthorizationService.checkDiaryViewable(groupMember.getLastViewableDiaryDate(), diary);
        commentAuthorizationService.checkCommentWritable(groupMember, diary);
        return CommentCreationVerifyResponse.from(groupMember.getProfileImage());
    }
}
