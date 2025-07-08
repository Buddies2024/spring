function addEventInCommentTextarea() {
    const textarea = document.querySelector(".comment-textarea");

    textarea.addEventListener("keydown", (event) => {
        if (!isIME(event)) {
            const selection = window.getSelection();
            const range = selection.getRangeAt(0);
            const { startContainer, startOffset } = range;

            if (event.key === "Backspace") {
                const isStart = startOffset === 0 && startContainer === textarea && selection.toString().length === 0;
                if (isStart) {
                    event.preventDefault();
                } else {
                    removeSticker(event, startContainer, startOffset);
                    removeNewLine(event, startContainer);
                }
            }
            adjustHeightByTextarea();
        }
    });
    textarea.addEventListener("keyup", (event) => {
        if (event.key === "Backspace") {
            if (textarea.innerHTML === "<br>") {
                textarea.innerHTML = "";
            }
        }
    })
}

function isIME(event) {
    return event.isComposing || event.key === "Process"
}

function removeSticker(event, startContainer, startOffset) {
    if (startContainer.nodeType === Node.ELEMENT_NODE) {
        const previousSibling = startContainer.childNodes[startOffset - 1];

        if (isCursorPositionBehindSticker(previousSibling)) {
            event.preventDefault();
            previousSibling.remove();
        }
    }
}

function isCursorPositionBehindSticker(element) {
    return element
        && element.nodeType === Node.ELEMENT_NODE
        && element.tagName === "DIV"
        && element.classList.contains("sticker-character");
}

function removeNewLine(event, startContainer) {
    if (startContainer.nodeType === Node.TEXT_NODE && startContainer.textContent === "\u200B") {
        event.preventDefault();
        if (startContainer.previousSibling && startContainer.previousSibling.tagName === "BR") {
            startContainer.previousSibling.remove();
        }
        startContainer.remove();
    }
}
