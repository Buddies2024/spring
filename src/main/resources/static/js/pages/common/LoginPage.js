import Component from "../../components/Component.js";
import { KAKAO_REDIRECT_URI, KAKAO_CLIENT_ID } from "../../common/env.js";

export default class LoginPage extends Component {
    template() {
        return `
        <div class="background login-page">
            <div class="content">
                <div class="logo">
                    <img class="logo-img" src="/images/login-page/spring.svg">
                </div>
                <div class="spring">
                    <span class="text main-text">우리만의 스프링을 만들어 볼까요?</span>
                </div>
                <div class="signup">
                    <span class="text signup-text">SNS 계정으로 간편 가입하기</span>
                </div>
                <a class="login-btn" href="https://kauth.kakao.com/oauth/authorize(response_type='code', redirect_uri=${KAKAO_REDIRECT_URI}, client_id=${KAKAO_CLIENT_ID})">
                    <div class="kakao-login">
                        <div class="kakao">
                            <img src="/images/login-page/kakao.svg">
                        </div>
                        <div class="login">
                            <span class="text login-text">카카오 로그인</span>
                        </div>
                    </div>
                </a>
            </div>
        </div>
        `;
    }
}
