function redirect(url) {
    window.location.href = url;
}

function replace(url) {
    window.location.replace(url);
}

document.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "visible") {
        if (typeof calendar === "undefined") {
            location.reload();
        } else {
            calendar.reload();
        }
    }
});
