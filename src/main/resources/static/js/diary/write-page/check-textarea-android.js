function addEventTextareasByAndroid() {
    if (window.visualViewport) {
        window.visualViewport.addEventListener("resize", () => {
            const height = window.visualViewport.height;
            if (height >= window.innerHeight) {
                document.activeElement.blur();
            }
        });
    }
    
    Array.from(textareas).forEach(textarea => {
        textarea.addEventListener("focus", () => { isActive = false });
        textarea.addEventListener("focusout", () => { isActive = true });
        textarea.addEventListener("click", closeModal);
        textarea.addEventListener("beforeinput", checkBeforeInputEvent);
        textarea.addEventListener("keydown", checkKeydownEvent);
        textarea.addEventListener("input", checkNextPage);
    });

    function checkBeforeInputEvent(event) {
        if (!canTyping) {
            event.preventDefault();
        } 
    }

    function checkKeydownEvent(event) {
        if (canTyping) {
            const textarea = event.target;
            prevValue = textarea.value;
            prevCursorpos = textarea.selectionStart;
        }
    }
    
    function checkNextPage(event) {
        const textarea = event.target;
        const text = event.data;

        if (textarea.scrollHeight > textarea.clientHeight) {
            if (canTyping && text && text.length > 1 && isKorean(text.slice(-2))) {
                prevValue = textarea.value.slice(0, -1);
            }
            textarea.value = prevValue;
            textarea.setSelectionRange(prevCursorpos, prevCursorpos);
    
            if (canTyping) {
                changeNextPage(textarea);
            }
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
            openNotificationModal("error", ["마지막 페이지까지 작성했어요!"], 2000);
            return;
        }
    
        if (textarea.selectionEnd !== textarea.value.length) {
            textarea.blur();
            openNotificationModal("error", ["더 이상 새로운 줄을 추가할 수 없어요!", "마지막 줄의 내용을 줄이거나", "다음 페이지로 넘어가주세요!"], 5000);
            return;
        }

        textarea.blur();
        const texts = textarea.value.split("\n");
        const lastText = texts[texts.length - 1].slice(-5)
        const result = await openConfirmModal(`"${lastText}" 까지 작성되었습니다.`, "다음 페이지로 넘어가시겠습니까?");
        if (result) {
            isActive = true;
            canTyping = false;
            changePageBySlide("next", "0.3s");
        }
    }
}
