package com.exchangediary.comment.ui;

import com.exchangediary.comment.service.CommentQueryService;
import com.exchangediary.comment.service.CommentCreateService;
import com.exchangediary.comment.service.ReplyCreateService;
import com.exchangediary.comment.ui.dto.request.CommentCreateRequest;
import com.exchangediary.comment.ui.dto.request.ReplyCreateRequest;
import com.exchangediary.comment.ui.dto.response.CommentCreateResponse;
import com.exchangediary.comment.ui.dto.response.CommentCreationVerifyResponse;
import com.exchangediary.comment.ui.dto.response.CommentResponse;
import com.exchangediary.diary.service.DiaryQueryService;
import com.exchangediary.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups/{groupId}/diaries/{diaryId}/comments")
public class ApiCommentController {
    private final DiaryQueryService diaryQueryService;
    private final CommentQueryService commentQueryService;
    private final CommentCreateService commentCreateService;
    private final ReplyCreateService replyCreateService;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<CommentCreateResponse> createComment(
            @PathVariable Long diaryId,
            @RequestBody @Valid CommentCreateRequest request,
            @RequestAttribute Long memberId
    ) {
        CommentCreateResponse response = commentCreateService.createComment(request, diaryId, memberId);
        notificationService.pushNotification(diaryQueryService.findDiary(diaryId).getMember().getId(), "친구가 내 일기에 댓글을 달았어요!");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<CommentCreationVerifyResponse> verifyCommentCreation(
            @PathVariable Long diaryId,
            @RequestAttribute Long memberId
    ) {
        CommentCreationVerifyResponse response = commentQueryService.verifyCommentCreation(diaryId, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> viewComment(
            @PathVariable Long diaryId,
            @PathVariable Long commentId,
            @RequestAttribute Long memberId
    ) {
        CommentResponse response = commentQueryService.viewComment(diaryId, memberId, commentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/{commentId}/replies")
    public ResponseEntity<Void> createReply(
            @RequestBody @Valid ReplyCreateRequest request,
            @PathVariable Long diaryId,
            @PathVariable Long commentId,
            @RequestAttribute Long memberId
    ) {
        replyCreateService.createReply(request, diaryId, commentId, memberId);
        notificationService.pushNotification(commentQueryService.findComment(commentId).getMember().getId(), "친구가 내 댓글에 답글을 달았어요!");
        notificationService.pushNotification(diaryQueryService.findDiary(diaryId).getMember().getId(), "친구가 내 일기의 댓글에 답글을 달았어요!");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
