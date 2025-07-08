package com.exchangediary.member.ui.dto.response;

public record KakaoTokenResponse(
        String token_type,
        String access_token,
        Integer expires_in,
        String refresh_token,
        Integer refresh_token_expires_in
) {
}
