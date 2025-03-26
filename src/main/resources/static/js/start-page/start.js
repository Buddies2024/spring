const logo = document.querySelector(".logo");
const logo_images = [
    "/images/start-page/line.gif",
    "/images/start-page/logo.png"
];

preLoadImgage(logo_images);

setTimeout(() => {
    logo.src = "/images/start-page/line.gif";
}, 10);

setTimeout(() => {
    logo.classList.add("end");
}, 2390);

document.addEventListener("click", () => {
    if (logo.classList.contains("end")) {
        startSpring();
    } else {
        logo.classList.add("end");
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
