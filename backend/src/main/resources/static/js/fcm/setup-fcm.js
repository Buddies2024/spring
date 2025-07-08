import { initializeApp } from 'https://www.gstatic.com/firebasejs/10.11.0/firebase-app.js'
import { getMessaging, getToken, onMessage } from 'https://www.gstatic.com/firebasejs/10.11.0/firebase-messaging.js'

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

export function registerServiceWorker() {
    if ("serviceWorker" in navigator) {
        window.addEventListener("load", function () {
            navigator.serviceWorker
                .register("/firebase-messaging-sw.js")
                .catch(() =>  console.log("Fail to register service worker"));
        });
    }
}

export async function setFCMToken() {
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

            if (/^\/groups\/[A-Za-z0-9]{8}$/.test(location.pathname)) {
                if (typeof calendar === "undefined") {
                    location.reload();
                } else {
                    calendar.reload();
                }
            }
        }
    });
}

handleMessage();
