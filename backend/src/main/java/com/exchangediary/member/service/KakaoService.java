package com.exchangediary.member.service;

import com.exchangediary.global.exception.ErrorCode;
import com.exchangediary.global.exception.serviceexception.KakaoLoginFailureException;
import com.exchangediary.member.ui.dto.request.KakaoTokenRequest;
import com.exchangediary.member.ui.dto.response.KakaoIdResponse;
import com.exchangediary.member.ui.dto.response.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@Transactional
public class KakaoService {
    private final static String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final static String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private final RestClient restClient = RestClient.create();
    @Value("${kakao.client_id}")
    private String clientId;
    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    public Long loginKakao(String code) {
        try {
            String token = getToken(code);
            KakaoIdResponse kakaoIdResponse = getKakaoUserInfo(token);
            return kakaoIdResponse.id();
        } catch (HttpClientErrorException exception) {
            throw new KakaoLoginFailureException(
                    ErrorCode.FAILED_TO_LOGIN_KAKAO,
                    "",
                    exception.getResponseBodyAsString()
            );
        }
    }

    private String getToken(String code) {
        KakaoTokenRequest kakaoTokenRequest = KakaoTokenRequest.from(clientId, redirectUri, code);
        KakaoTokenResponse kakaoTokenResponse = requestToken(kakaoTokenRequest);
        return kakaoTokenResponse.access_token();
    }

    private KakaoTokenResponse requestToken(KakaoTokenRequest kakaoTokenRequest) {
        return restClient.post()
                .uri(KAKAO_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(kakaoTokenRequest.convertMap())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new KakaoLoginFailureException(
                            ErrorCode.FAILED_TO_ISSUE_KAKAO_TOKEN,
                            "",
                            response.getBody().toString()
                    );
                })
                .body(KakaoTokenResponse.class);
    }

    private KakaoIdResponse getKakaoUserInfo(String token) {
        return restClient.get()
                .uri(KAKAO_USER_INFO_URL)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new KakaoLoginFailureException(
                            ErrorCode.FAILED_TO_GET_KAKAO_USER_INFO,
                            "",
                            response.getBody().toString()
                    );
                })
                .body(KakaoIdResponse.class);
    }
}
