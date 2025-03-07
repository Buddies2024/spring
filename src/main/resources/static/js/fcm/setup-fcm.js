import { initializeApp } from 'https://www.gstatic.com/firebasejs/10.11.0/firebase-app.js'
import { getMessaging, getToken, onMessage } from 'https://www.gstatic.com/firebasejs/10.11.0/firebase-messaging.js'

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

function setupFCM() {
    registerServiceWorker();
    requestNotificationPermission();
    handleMessage();
}

function registerServiceWorker() {
    if ("serviceWorker" in navigator) {
        window.addEventListener("load", function () {
            navigator.serviceWorker
                .register("/firebase-messaging-sw.js")
                .catch(() =>  console.log("Fail to register service worker"));
        });
    }
}

export function requestNotificationPermission() {
    Notification.requestPermission().then((permission) => {
        if (permission === 'granted') {
            console.log('알림 권한이 허용되어 있습니다.');
            setFCMToken();
        } else {
            console.log('알림 권한이 차단되어 있습니다.');
        }
    }).catch(function (err) {
        console.log('알림 권한을 조회하던 도중 에러가 발생했습니다.', err);
    });
}

function setFCMToken() {
    getToken(messaging, { vapidKey: vapidKey })
    .then(function (currentToken) {
        if (currentToken) {
            console.log(currentToken);
            sendTokenToServer(currentToken);
        } else {
            console.log("토큰 등록이 불가능 합니다.")
        }
    }).catch(function (err) {
        console.log('토큰을 가져올 수 없습니다.', err);
    });
}

function sendTokenToServer(token) {
    fetch("/api/members/notifications/token", {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            "token": token
        })
    });
}

function handleMessage() {
    onMessage(messaging, (payLoad) => {
        console.log("알림 도착");
        console.log(payLoad);
        var notificationTitle = payLoad.notification.title;
        var notificationOptions = {
            body: payLoad.notification.body,
            icon: payLoad.notification.icon,
        };
        if (document.visibilityState === 'visible') {
            new Notification(notificationTitle, notificationOptions);
        }
    });
}

setupFCM();
