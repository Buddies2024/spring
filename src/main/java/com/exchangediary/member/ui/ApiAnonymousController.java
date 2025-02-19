package com.exchangediary.member.ui;

import com.exchangediary.member.service.AnonymousService;
import com.exchangediary.member.ui.dto.response.AnonymousInfoResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/anonymous")
public class ApiAnonymousController {
    private final AnonymousService anonymousService;

    @GetMapping("/info")
    public ResponseEntity<AnonymousInfoResponse> viewAnonymousInfo(
            @CookieValue(value = "token", required = false) String token,
            HttpServletResponse response
    ) {
        AnonymousInfoResponse anonymousInfoResponse = anonymousService.viewAnonymousInfo(token, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(anonymousInfoResponse);
    }
}
