const textareas = Array.from(document.querySelectorAll(".diary-content"));
var canTyping = true;
var prevValue = "";
var prevCursorpos = 0;

initCheckTextarea();

function initCheckTextarea() {
    const mobileOS = getMobileOS();

    if (mobileOS === "iOS") {
        addEventTextareasByIos();
    }

    if (mobileOS === "Android") {
        addEventTextareasByAndroid();
    }
}
