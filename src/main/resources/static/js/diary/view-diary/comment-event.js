const notificationModal = document.querySelector(".notification-modal");
const confirmModal = document.querySelector(".confirm-modal");
let previousCommentTextHeight = 20;

function clickCommentBlur(event) {
    if (commentBtn.classList.contains("selected") && event.target.classList.contains("comment-blur")) {
        event.preventDefault();
        offClickCommentBtn();
        commentBtn.classList.remove("selected");
    }
}

function clickWriteCommentOutside(event) {
    event.preventDefault();

    const commentCharacter = document.querySelector(".comment-area .comment-character");
    const comment = document.querySelector(".comment");

    if (!isInModal(event.target)
        && event.target !== commentCharacter
        && !comment.contains(event.target)
    ) {
        commentCharacter.remove();
        commentArea.classList.remove("write");
        commentBtn.classList.remove("selected");
        comment.remove();
        previousCommentTextHeight = 20;
        document.removeEventListener("click", clickWriteCommentOutside);
    }
}

function clickWrittenCommentOutside(event) {
    event.preventDefault();

    const viewCommentCharacter = document.querySelector(".note-content .comment-character:not(.written)");
    const comment = document.querySelector(".comment");

    if (!isInModal(event.target)
        && event.target !== viewCommentCharacter
        && !comment.contains(event.target)
    ) {
        viewCommentCharacter.classList.add("written");
        document.querySelector(".comment").remove();
        previousCommentTextHeight = 20;
        document.removeEventListener("click", clickWrittenCommentOutside);
    }
}

function isInModal(target) {
    return notificationModal.contains(target) || confirmModal.contains(target);
}

function adjustCommentBoxHeightByTextarea() {
    const commentText = document.querySelector(".comment-textarea");

    var maximumHeight = 40;
    if (commentBtn.classList.contains("selected")) {
        maximumHeight = 100;
    }

    if (commentText.scrollHeight > maximumHeight) {
        commentText.style.overflow = "scroll";
        commentText.style.height = `${maximumHeight}px`;
        previousCommentTextHeight = maximumHeight;
        return ;
    }

    const commentTextHeight = parseInt(commentText.scrollHeight / 20) * 20;
    commentText.style.height = "auto";
    commentText.style.height = `${commentTextHeight}px`;

    if (commentTextHeight > 0 && commentTextHeight !== previousCommentTextHeight) {
        const gap = commentTextHeight > previousCommentTextHeight ? 20 : -20;

        commentText.style.overflow = "none";
        previousCommentTextHeight = commentTextHeight;

        const comment = document.querySelector(".comment");
        const commentBox = document.querySelector(".comment-box");

        comment.style.height = `${parseInt(comment.style.height) + gap}px`;
        commentBox.style.height = `${parseInt(commentBox.style.height) + gap}px`;

        if (!commentBtn.classList.contains("selected")) {
            const replyBox = document.querySelector(".reply-box");
            replyBox.style.height = `${parseInt(replyBox.style.height) + gap}px`;
        }
    }
}
