package com.exchangediary.comment.service;

import com.exchangediary.comment.domain.ReplyRepository;
import com.exchangediary.comment.domain.entity.Comment;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.service.DiaryAuthorizationService;
import com.exchangediary.diary.service.DiaryQueryService;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.service.GroupMemberQueryService;
import com.exchangediary.comment.domain.entity.Reply;
import com.exchangediary.comment.ui.dto.request.ReplyCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyCreateService {
    private final CommentQueryService commentQueryService;
    private final DiaryQueryService diaryQueryService;
    private final DiaryAuthorizationService diaryAuthorizationService;
    private final GroupMemberQueryService groupMemberQueryService;
    private final ReplyRepository replyRepository;

    public void createReply(ReplyCreateRequest request, Long diaryId, Long commentId, Long memberId) {
        GroupMember groupMember = groupMemberQueryService.findGroupMemberByMemberId(memberId);
        Diary diary = diaryQueryService.findDiary(diaryId);

        diaryAuthorizationService.checkDiaryViewable(groupMember.getLastViewableDiaryDate(), diary);
        Comment comment = commentQueryService.findComment(commentId);
        Reply reply = Reply.of(request.content(), groupMember, comment);
        replyRepository.save(reply);
    }
}
