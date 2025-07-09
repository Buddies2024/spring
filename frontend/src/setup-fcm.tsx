import axios from "axios";
import { initializeApp } from "firebase/app";
import { getMessaging, getToken, MessagePayload, onMessage } from "firebase/messaging";

const firebaseConfig = {
  apiKey: "AIzaSyAqVv93bD1Po8OHgZhZXfaPBOImUN8ZTNg",
  authDomain: "buddies-spring.firebaseapp.com",
  projectId: "buddies-spring",
  storageBucket: "buddies-spring.firebasestorage.app",
  messagingSenderId: "271822946508",
  appId: "1:271822946508:web:3cf3224415f64c2e8b8269",
  measurementId: "G-QSJPG39DJD"
};
const vapidKey = "BBVTNddie0h-LECfkubVgahjMzB2EXNL8ISVQqtWYYUiSYIL-it9g29Jvb43J8e81PwHUiR2ARbbMIvuXBdzuhE";
const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

function registerServiceWorker() {
    if ("serviceWorker" in navigator) {
        window.addEventListener("load", function () {
            navigator.serviceWorker
                .register("/firebase-messaging-sw.js")
                .catch(() =>  console.log("Fail to register service worker"));
        });
    } else {
        console.warn('이 브라우저는 서비스 워커를 지원하지 않습니다.');
        alert('푸시 알림을 위해 서비스 워커를 지원하는 브라우저를 사용해주세요.');
    }
}

async function setFCMToken() {
    try {
        const currentToken = await getToken(messaging, { vapidKey: vapidKey });

        if (currentToken) {
            console.log(currentToken);
            sendTokenToServer(currentToken);
        } else {
            console.log("토큰 등록이 불가능 합니다.");
        }
    } catch (err) {
        console.log('토큰을 가져올 수 없습니다.', err);
    }
}

function sendTokenToServer(token: string) {
    const url = "/api/members/notifications/token";
    const body = { "token": token };
    const header = {
        headers: {
          "Content-Type": "application/json"
        }
    };
    axios.patch(url, body, header)
        .then(response => {
            console.log("FCM 토큰 서버 전송 성공 : ", response.data);
        })
        .catch(error => {
            console.error(error.message);
        });
}

function handleMessage() {
    onMessage(messaging, (payLoad: MessagePayload) => {
        console.log("알림 도착");
        console.log(payLoad);
        var notificationTitle = payLoad.notification?.title || "";
        var notificationOptions = {
            body: payLoad.notification?.body,
            icon: payLoad.notification?.icon,
        };
        if (document.visibilityState === 'visible') {
            const notification = new Notification(notificationTitle, notificationOptions);

            notification.onclick = (event) => {
                event.preventDefault();
                notification.close();
                const targetUrl = notification.data?.url || "/";
                window.location.href = targetUrl;
            }
            // if (/^\/groups\/[A-Za-z0-9]{8}$/.test(location.pathname)) {
            //     if (typeof calendar === "undefined") {
            //         location.reload();
            //     } else {
            //         calendar.reload();
            //     }
            // }
        }
    });
}

export { registerServiceWorker, setFCMToken, handleMessage };
