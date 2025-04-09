import { registerServiceWorker, setFCMToken } from "/js/fcm/setup-fcm.js"

const logo = document.querySelector(".logo");
const logo_images = [
    "/images/start-page/line.gif",
    "/images/start-page/logo.png"
];
const startPrompt = document.querySelector(".start-prompt");

preLoadImgage(logo_images);
registerServiceWorker();

window.addEventListener("load", init);

function init() {
    setTimeout(() => {
        logo.src = "/images/start-page/line.gif";
    }, 10);
    
    setTimeout(() => {
        logo.classList.add("end");
        drawStartPrompt();
    }, 2400);
    
    document.addEventListener("click", () => {
        if (logo.classList.contains("end")) {
            requestNotificationPermission();
        } else {
            logo.classList.add("end");
            drawStartPrompt();
        }
    });
}

async function requestNotificationPermission() {
    try {
        const permission = await Notification.requestPermission();

        if (permission === 'granted') {
            console.log('알림 권한이 허용되어 있습니다.');
            await setFCMToken();
        } else {
            console.log('알림 권한이 차단되어 있습니다.');
        }
    } catch (err) {
        console.log('알림 권한을 조회하던 도중 에러가 발생했습니다.', err);
    }

    startSpring();
}

function startSpring() {
    fetch(`/api/anonymous/info`)
    .then(response => response.json())
    .then(data => redirect(getUrl(data)));
}

function getUrl(anonymousInfo) {
    window.localStorage.removeItem("groupId");

    if (anonymousInfo.shouldLogin) {
        return "/login"
    }

    if (anonymousInfo.groupId === null) {
        return "/groups"
    }

    window.localStorage.setItem("groupId", anonymousInfo.groupId);
    return `/groups/${anonymousInfo.groupId}`
}

function drawStartPrompt() {
    startPrompt.innerText = "화면을 눌러 시작하기";
    startPrompt.classList.add("typing");

    setTimeout(() => {
        startPrompt.classList.replace("typing", "blinking-text")
    }, 1000);
}
