function addEventTextareasByIos() {
    Array.from(textareas).forEach(textarea => {
        textarea.addEventListener("focus", (event) => {
            event.target.classList.add("focus");
            isActive = false;
        });
        textarea.addEventListener("focusout", (event) => { 
            event.target.classList.remove("focus");
            isActive = true;
        });
        textarea.addEventListener("click", closeModal);
        textarea.addEventListener("keydown", checkKeydownEvent);
        textarea.addEventListener("input", checkNextPage);
    });

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
        if (textarea.scrollHeight > textarea.clientHeight) {
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
    
    async function changeNextPage(textarea) {
        const index = textarea.getAttribute("data-id");
    
        if (index === "5") {
            textarea.blur();
            openNotificationModal("error", ["더 이상 글자를 입력할 수 없어요.", "못다 한 이야기는 다음 순서에!"], 2000);
            return;
        }
    
        if (textarea.selectionEnd !== textarea.value.length) {
            textarea.blur();
            openNotificationModal("error", ["이 페이지는 가득 차서", "새로운 문장을 추가할 수 없어요."], 2000);
            return;
        }

        textarea.blur();
        const texts = textarea.value.split("\n");
        const lastText = texts[texts.length - 1].slice(-5)
        const result = await openConfirmModal("페이지를 넘길까요?", `이 페이지는 "${lastText}" 까지 작성되었어요.`);
        if (result) {
            isActive = true;
            canTyping = false;
            changePageBySlide("next", "0.3s");
        }
    }
}
