function reloadMonthlyPage() {
    if (document.visibilityState === "visible") {
        clearDate();
        drawDateOfCalendar();
        drawBottom();
    }
}

document.addEventListener("visibilitychange", reloadMonthlyPage);
