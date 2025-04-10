var canTyping = true;
var prevValue = "";
var prevCursorpos = 0;

function addEventTextareasByIos() {
    Array.from(textareas).forEach(textarea => {
        textarea.addEventListener("focus", () => { isActive = false });
        textarea.addEventListener("focusout", () => { isActive = true });
        textarea.addEventListener("click", closeModal);
        textarea.addEventListener("keydown", checkKeydownEvent);
        textarea.addEventListener("input", checkNextPage);
    });
}

function checkKeydownEvent(event) {
    if (!canTyping) {
        event.preventDefault();
    } else {
        const textarea = event.target;
        prevValue = textarea.value;
        prevCursorpos = textarea.selectionStart;
    }
}

function checkNextPage(event) {
    const textarea = event.target;
    const text = event.data;
    if (canTyping && textarea.scrollHeight > textarea.clientHeight) {
        if (isKorean(text) && text.length == 2) {
            prevValue = textarea.value.slice(0, -1);
        }
        textarea.value = prevValue;
        textarea.setSelectionRange(prevCursorpos, prevCursorpos);

        changeNextPage(textarea);
    }
}

function isKorean(text) {
    const koreanRegex = /[\u1100-\u11FF\u3130-\u318F\uAC00-\uD7AF]/g;

    return koreanRegex.test(text);
}

function changeNextPage(textarea) {
    const index = textarea.getAttribute("data-id");

    if (index === "5") {
        textarea.blur();
        openNotificationModal("error", ["마지막 페이지까지 작성했어요!"], 2000);
        return;
    }

    if (textarea.selectionEnd !== textarea.value.length) {
        textarea.blur();
        openNotificationModal("error", ["더 이상 새로운 줄을 추가할 수 없어요!", "마지막 줄의 내용을 줄이거나", "다음 페이지로 넘어가주세요!"], 5000);
        return;
    }

    isActive = true;
    canTyping = false;
    changePageBySlide("next", "0.3s");
}
