function redirect(url) {
    window.location.href = url;
}

function replace(url) {
    window.location.replace(url);
}

document.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "visible" && !/^\/groups\/[A-Za-z0-9]{8}\/diaries$/.test(location.pathname)) {
        if (typeof calendar === "undefined") {
            location.reload();
        } else {
            calendar.reload();
        }
    }
});
