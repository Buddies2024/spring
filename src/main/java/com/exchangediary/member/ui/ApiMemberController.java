package com.exchangediary.member.ui;

import com.exchangediary.member.service.MemberQueryService;
import com.exchangediary.member.ui.dto.response.MemberNotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class ApiMemberController {
    private final MemberQueryService memberQueryService;

    @GetMapping("/notification")
    public ResponseEntity<MemberNotificationResponse> getMemberNotificationOn(@RequestAttribute Long memberId) {
        MemberNotificationResponse body = memberQueryService.getOnNotification(memberId);
        return ResponseEntity
                .ok(body);
    }
}
