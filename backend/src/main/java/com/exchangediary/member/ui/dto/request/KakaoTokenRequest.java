package com.exchangediary.member.ui.dto.request;

import lombok.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder
public record KakaoTokenRequest(
        String grant_type,
        String client_id,
        String redirect_uri,
        String code
) {
    public static KakaoTokenRequest from(String client_id, String redirect_uri, String  code) {
        return KakaoTokenRequest.builder()
                .grant_type("authorization_code")
                .client_id(client_id)
                .redirect_uri(redirect_uri)
                .code(code)
                .build();
    }

    public MultiValueMap<String, String> convertMap() {
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();

        map.add("grant_type", grant_type);
        map.add("client_id", client_id);
        map.add("redirect_uri", redirect_uri);
        map.add("code", code);
        return map;
    }
}
