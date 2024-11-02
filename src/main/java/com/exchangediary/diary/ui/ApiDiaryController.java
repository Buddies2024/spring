package com.exchangediary.diary.ui;

import com.exchangediary.diary.service.DiaryWriteService;
import com.exchangediary.diary.service.DiaryQueryService;
import com.exchangediary.diary.ui.dto.request.DiaryRequest;
import com.exchangediary.diary.ui.dto.response.DiaryResponse;
import com.exchangediary.diary.ui.dto.response.DiaryWritableStatusResponse;
import com.exchangediary.diary.ui.dto.response.DiaryMonthlyResponse;
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

    @PostMapping
    public ResponseEntity<Void> writeDiary(
            @RequestPart(name = "data") @Valid DiaryRequest diaryRequest,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @PathVariable Long groupId,
            @RequestAttribute Long memberId
    ) {
        Long diaryId = diaryWriteService.writeDiary(diaryRequest, file, groupId, memberId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Content-Location", "/groups/" + groupId + "/diaries/" + diaryId)
                .build();
    }

    @GetMapping("/monthly")
    public ResponseEntity<DiaryMonthlyResponse> viewMonthlyDiary(
            @RequestParam int year,
            @RequestParam int month,
            @PathVariable Long groupId,
            @RequestAttribute Long memberId
    ) {
        DiaryMonthlyResponse diaryMonthlyResponse = diaryQueryService.viewMonthlyDiary(year, month, groupId, memberId);
        return ResponseEntity
                .ok()
                .body(diaryMonthlyResponse);
    }

    @GetMapping("/status")
    public ResponseEntity<DiaryWritableStatusResponse> getDiaryWritableStatus(
            @PathVariable Long groupId,
            @RequestAttribute Long memberId
    ) {
        DiaryWritableStatusResponse response = diaryQueryService.getMembersDiaryAuthorization(groupId, memberId);
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
