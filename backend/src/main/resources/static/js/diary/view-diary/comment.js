const commentBtn = document.querySelector(".comment-btn");
const commentArea = document.querySelector(".comment-area");
const currentPathName = window.location.pathname;

init();

function init() {
    commentBtn.addEventListener("click", () => openNotificationModal("yellow", ["COMING SOON", "댓글 기능을 찾아내셨군요!"], 2000));
    // commentBtn.addEventListener("click", clickCommentBtn);
    // document.addEventListener("click", clickCommentBlur);
}

function clickCommentBlur(event) {
    if (commentBtn.classList.contains("selected") && event.target.classList.contains("comment-blur")) {
        event.preventDefault();
        offClickCommentBtn();
        commentBtn.classList.remove("selected");
    }
}
