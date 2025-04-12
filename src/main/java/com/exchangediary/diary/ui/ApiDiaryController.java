package com.exchangediary.diary.ui;

import com.exchangediary.diary.service.DiaryWriteService;
import com.exchangediary.diary.service.DiaryQueryService;
import com.exchangediary.diary.ui.dto.notification.DiaryWriteNotification;
import com.exchangediary.diary.ui.dto.request.DiaryRequest;
import com.exchangediary.diary.ui.dto.response.DiaryResponse;
import com.exchangediary.diary.ui.dto.response.TodayDiaryStatusResponse;
import com.exchangediary.diary.ui.dto.response.DiaryMonthlyResponse;
import com.exchangediary.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups/{groupId}/diaries")
public class ApiDiaryController {
    private final DiaryWriteService diaryWriteService;
    private final DiaryQueryService diaryQueryService;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Void> writeDiary(
            @RequestPart(name = "data") @Valid DiaryRequest diaryRequest,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @PathVariable String groupId,
            @RequestAttribute Long memberId
    ) {
        DiaryWriteNotification diary = diaryWriteService.writeDiary(diaryRequest, file, groupId, memberId);

        notificationService.pushToAllGroupMembersExceptMember(
                groupId,
                memberId,
                String.format("%s(이)가 일기를 작성했어요.", diary.writerNickname())
        );
        notificationService.pushDiaryOrderNotification(groupId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Content-Location", "/groups/" + groupId + "/diaries/" + diary.diaryId())
                .build();
    }

    @GetMapping("/monthly")
    public ResponseEntity<DiaryMonthlyResponse> viewMonthlyDiary(
            @RequestParam int year,
            @RequestParam int month,
            @PathVariable String groupId,
            @RequestAttribute Long memberId
    ) {
        DiaryMonthlyResponse diaryMonthlyResponse = diaryQueryService.viewMonthlyDiary(year, month, groupId, memberId);
        return ResponseEntity
                .ok()
                .body(diaryMonthlyResponse);
    }

    @GetMapping("/today")
    public ResponseEntity<TodayDiaryStatusResponse> getTodayDiaryStatus(
            @PathVariable String groupId,
            @RequestAttribute Long memberId
    ) {
        TodayDiaryStatusResponse response = diaryQueryService.getTodayDiaryStatus(groupId, memberId);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryResponse> viewDiary(
            @PathVariable Long diaryId,
            @RequestAttribute Long memberId
    ) {
        DiaryResponse diaryResponse = diaryQueryService.viewDiary(memberId, diaryId);
        return ResponseEntity
                .ok()
                .body(diaryResponse);
    }
}
