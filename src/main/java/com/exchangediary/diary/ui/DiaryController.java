package com.exchangediary.diary.ui;

import com.exchangediary.diary.service.DiaryAuthorizationService;
import com.exchangediary.diary.service.DiaryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/diaries")
public class DiaryController {
    private final DiaryAuthorizationService diaryAuthorizationService;
    private final DiaryQueryService diaryQueryService;

    @GetMapping
    public String writePage(
            Model model,
            @PathVariable String groupId,
            @RequestAttribute Long memberId
    ) {
        diaryAuthorizationService.checkDiaryWritable(groupId, memberId);
        model.addAttribute("groupId", groupId);
        return "diary/write-page";
    }

}
