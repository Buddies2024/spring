import { registerServiceWorker, requestNotificationPermission } from "/js/fcm/setup-fcm.js"

const logo = document.querySelector(".logo");
const logo_images = [
    "/images/start-page/line.gif",
    "/images/start-page/logo.png"
];
const startPrompt = document.querySelector(".start-prompt");

preLoadImgage(logo_images);
registerServiceWorker();

setTimeout(() => {
    logo.src = "/images/start-page/line.gif";
}, 10);

setTimeout(() => {
    logo.classList.add("end");
    drawStartPrompt();
}, 2400);

document.addEventListener("click", () => {
    if (logo.classList.contains("end")) {
        requestNotificationPermission(startSpring);
    } else {
        logo.classList.add("end");
        drawStartPrompt();
    }
});

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
