function adjustHeightByTextarea() {
    const commentText = document.querySelector(".comment-textarea");
    commentText.style.height = "auto";
    const commentTextHeight = parseInt(commentText.scrollHeight / 20) * 20 + 4;
    const maximumHeight = commentBtn.classList.contains("selected") ? 104 : 44;

    if (commentTextHeight > maximumHeight) {
        commentText.style.overflow = "scroll";
        commentText.style.height = `${maximumHeight}px`;
        previousCommentTextHeight = maximumHeight;
        return ;
    }

    commentText.style.height = `${commentTextHeight}px`;

    if (commentTextHeight > 0 && commentTextHeight !== previousCommentTextHeight) {
        const gap = 20 * parseInt((commentTextHeight - previousCommentTextHeight) / 20);

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

function adjustHeightByWrittenComment() {
    const commentText = document.querySelector(".written-comment-text");
    const comment = document.querySelector(".comment");
    const commentBox = document.querySelector(".comment-box");
    const writtenComment = document.querySelector(".written-comment");

    if (commentText.offsetHeight > 40) {
        commentText.style.height = "40px";
        commentText.style.overflow = "scroll";
    }
    comment.style.height = `${parseInt(comment.style.height) + (commentText.offsetHeight - 26)}px`;
    commentBox.style.height = `${parseInt(commentBox.style.height) + (commentText.offsetHeight - 26)}px`;
    writtenComment.style.height = `${parseInt(writtenComment.style.height) + (commentText.offsetHeight - 26)}px`;
}

function adjustHeightByWrittenReplies() {
    const repliesText = document.querySelectorAll(".reply-text");

    if (repliesText.length > 0) {
        const replyBox = document.querySelector(".reply-box");
        const comment = document.querySelector(".comment");
        const commentBox = document.querySelector(".comment-box");
        var repliesHeight = 0;
        var lastReplyHeight;

        repliesText.forEach(replyText => {
            repliesHeight += replyText.offsetHeight;

            if (parseInt(replyText.offsetHeight / 20) === 1) {
                if (replyText.offsetHeight === 20) {
                    repliesHeight += 7;
                } else {
                    repliesHeight += 14;
                }

                replyText.parentElement.style.height = `${replyText.offsetHeight + 7}px`;
            } else {
                if (replyText.offsetHeight % 20 !== 0) {
                    repliesHeight += 6;
                }
                replyText.style.marginTop = "-3px";

                const reply = replyText.parentElement;

                reply.style.height = `${replyText.offsetHeight}px`;
                reply.style.marginBottom = "2px";
            }
            lastReplyHeight = replyText.offsetHeight;
        });

        changeRepliesStyle(repliesHeight, lastReplyHeight);
        const gap = getIncreaseOfHeight(repliesHeight, lastReplyHeight);

        replyBox.style.height = `${parseInt(replyBox.style.height) + gap}px`;
        comment.style.height = `${parseInt(comment.style.height) + gap}px`;
        commentBox.style.height = `${parseInt(commentBox.style.height) + gap}px`;
    }
}

function changeRepliesStyle(repliesHeight, lastReplyHeight) {
    const replies = document.querySelector(".replies");

    if (lastReplyHeight === 20 && repliesHeight < MAXIMUM_REPLY_BOX_HEIGHT) {
        replies.style.height = `${repliesHeight + 7}px`;
    } else {
        replies.style.height = `${repliesHeight}px`;
    }
    if (repliesHeight > MAXIMUM_REPLY_BOX_HEIGHT) {
        replies.style.overflow = "scroll";
        replies.style.height = `${MAXIMUM_REPLY_BOX_HEIGHT}px`;
    }
    if (lastReplyHeight > 20) {
        replies.style.marginBottom = "6px";
    }
}

function getIncreaseOfHeight(repliesHeight, lastReplyHeight) {
    if (repliesHeight >= MAXIMUM_REPLY_BOX_HEIGHT) {
        return MAXIMUM_REPLY_BOX_HEIGHT + 24;
    }
    if (lastReplyHeight > 20) {
        return repliesHeight + 23;
    }
    return repliesHeight + 30;
}
