const textareas = Array.from(document.querySelectorAll(".diary-content"));
const textareaValues = textareas.map(textarea => textarea.value);
var isMoving = false;
addEventTextareas();

function addEventTextareas() {
    Array.from(textareas).forEach(textarea => {
        textarea.addEventListener("focus", () => { isActive = false });
        textarea.addEventListener("focusout", () => { isActive = true });
        textarea.addEventListener("click", closeModal);
        textarea.addEventListener("keydown", checkMovePage);
        textarea.addEventListener("input", checkNextPage);
        textarea.addEventListener("compositionstart", checkNextPageByKorean);
    });
}

function checkMovePage(event) {
    if (isMoving) {
        event.preventDefault();
    }
}

function checkNextPage(event) {
    if (!event.isComposing) {
        checkOverPage(event);
    }
}

function checkNextPageByKorean(event) {
    requestAnimationFrame(() => {
        checkOverPage(event);
    });
}

function checkOverPage(event) {
    const textarea = event.target;
    const index = textareas.indexOf(textarea);
    if (textarea.scrollHeight !== textarea.offsetHeight) {
        if (textarea.value.length === textareaValues[index].length) {
            textareaValues[index] = textareaValues[index].slice(0, -1);
        }
        const selection = textarea.selectionEnd - 1;
        textarea.value = textareaValues[index];
        textarea.selectionEnd = selection;

        if (canTurnPage(index, event)) {
            isActive = true;
            isMoving = true;
            changePageBySlide("next", "0.3s");
        }
    }
    textareaValues[index] = textarea.value;
}

function canTurnPage(index, event) {
    const textarea = event.target;
    if (textarea.selectionEnd !== textarea.value.length) {
        return false;
    }
    return index < 4;
}
