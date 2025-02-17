package com.exchangediary.comment.service;

import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.comment.domain.CommentRepository;
import com.exchangediary.comment.ui.dto.request.CommentCreateRequest;
import com.exchangediary.comment.ui.dto.response.CommentCreateResponse;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.service.DiaryAuthorizationService;
import com.exchangediary.diary.service.DiaryQueryService;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCreateService {
    private final MemberQueryService memberQueryService;
    private final DiaryQueryService diaryQueryService;
    private final DiaryAuthorizationService diaryAuthorizationService;
    private final CommentRepository commentRepository;
    private final CommentAuthorizationService commentAuthorizationService;

    public CommentCreateResponse createComment(CommentCreateRequest request, Long diaryId, Long memberId) {
        Member member = memberQueryService.findMember(memberId);
        Diary diary = diaryQueryService.findDiary(diaryId);

        diaryAuthorizationService.checkDiaryViewable(member, diary);
        commentAuthorizationService.checkCommentWritable(member, diary);
        Comment comment = Comment.of(request, member, diary);
        commentRepository.save(comment);
        return CommentCreateResponse.of(comment, member.getProfileImage());
    }
}
