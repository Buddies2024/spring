const notificationModal = document.querySelector(".notification-modal");
const confirmModal = document.querySelector(".confirm-modal");
let previousCommentTextHeight = 24;

function clickWriteCommentOutside(event) {
    event.preventDefault();

    const commentCharacter = document.querySelector(".comment-area .comment-character");
    const comment = document.querySelector(".comment");

    if (!isInModal(event.target)
        && event.target !== commentCharacter
        && comment && !comment.contains(event.target)
    ) {
        commentCharacter.remove();
        commentArea.classList.remove("write");
        commentBtn.classList.remove("selected");
        comment.remove();
        previousCommentTextHeight = 24;
        document.removeEventListener("click", clickWriteCommentOutside);
    }
}

function clickWrittenCommentOutside(event) {
    event.preventDefault();

    const viewCommentCharacter = document.querySelector(".note-content .comment-character:not(.written)");
    const comment = document.querySelector(".comment");

    if (!isInModal(event.target)
        && event.target !== viewCommentCharacter
        && comment && !comment.contains(event.target)
    ) {
        viewCommentCharacter.classList.add("written");
        document.querySelector(".comment").remove();
        previousCommentTextHeight = 24;
        isActive = true;
        document.removeEventListener("click", clickWrittenCommentOutside);
    }
}

function isInModal(target) {
    return notificationModal.contains(target) || confirmModal.contains(target);
}
