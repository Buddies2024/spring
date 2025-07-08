package com.exchangediary.comment.service;

import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.ui.dto.request.CommentCreateRequest;
import com.exchangediary.comment.ui.dto.response.CommentCreateResponse;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.service.DiaryAuthorizationService;
import com.exchangediary.diary.service.DiaryQueryService;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.service.GroupMemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCreateService {
    private final CommentAuthorizationService commentAuthorizationService;
    private final DiaryQueryService diaryQueryService;
    private final DiaryAuthorizationService diaryAuthorizationService;
    private final GroupMemberQueryService groupMemberQueryService;
    private final CommentRepository commentRepository;

    public CommentCreateResponse createComment(CommentCreateRequest request, Long diaryId, Long memberId) {
        GroupMember groupMember = groupMemberQueryService.findGroupMemberByMemberId(memberId);
        Diary diary = diaryQueryService.findDiary(diaryId);

        diaryAuthorizationService.checkDiaryViewable(groupMember.getLastViewableDiaryDate(), diary);
        commentAuthorizationService.checkCommentWritable(groupMember, diary);
        Comment comment = Comment.of(request.xCoordinate(), request.yCoordinate(), request.page(), request.content(), groupMember, diary);
        commentRepository.save(comment);
        return CommentCreateResponse.of(comment, groupMember.getProfileImage());
    }
}
