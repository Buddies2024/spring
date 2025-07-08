async function clickWriteCommentBtn(event) {
    event.preventDefault();

    const commentText = document.querySelector(".comment-textarea");

    if (NEWLINE_REGEX.test(commentText.value) || WHITESPACE_REGEX.test(commentText.value)) {
        openNotificationModal("error", ["댓글 내용을 입력해주세요."], 2000);
    } else {
        const result = await openConfirmModal("댓글을 작성할까요?", "댓글은 수정, 삭제가 불가하니 신중하게 결정해 주세요.");

        if (result) {
            writeComment(commentText.innerHTML);
        }
    }
}

function writeComment(commentContent) {
    const commentCharacter = document.querySelector(".write .comment-character");

    fetch(`/api${currentPathName}/comments`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            "xCoordinate": parseFloat(commentCharacter.style.left) + 5,
            "yCoordinate": parseFloat(commentCharacter.style.top) - 18,
            "page": currentPage.index,
            "content": commentContent
        })
    })
        .then(async response => {
            if (response.status !== 201) {
                throw await response.json();
            }
            window.location.reload();
        })
        .catch(data => {
            openNotificationModal("error", [data.message], 2000);
        });
}

async function clickWriteReplyBtn(event) {
    event.preventDefault();

    const commentText = document.querySelector(".reply-bar .comment-textarea");

    if (NEWLINE_REGEX.test(commentText.value) || WHITESPACE_REGEX.test(commentText.value)) {
        openNotificationModal("error", ["답글 내용을 입력해주세요."], 2000);
    } else {
        const result = await openConfirmModal("답글을 작성할까요?", "답글은 수정, 삭제가 불가하니 신중하게 결정해 주세요.");

        if (result) {
            writeReply(commentText.innerHTML);
        }
    }
}

function writeReply(replyContent) {
    const commentCharacter = document.querySelector(".note-content .comment-character:not(.written)");
    const commentId = commentCharacter.classList[2];

    fetch(`/api${currentPathName}/comments/${commentId}/replies`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            "content": replyContent
        })
    })
        .then(async response => {
            if (response.status !== 201) {
                throw await response.json();
            }
            window.location.reload();
        })
        .catch(data => {
            openNotificationModal("error", [data.message], 2000);
        });
}
