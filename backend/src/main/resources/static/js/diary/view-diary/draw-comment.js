const STANDARD_TOP = 137;
const STANDARD_LEFT = 76;
const MAXIMUM_REPLY_BOX_HEIGHT = 100;
const NEWLINE_REGEX = /^[\r\n]*$/;
const WHITESPACE_REGEX = /^\s+$/;
const COMMENT_COMMENT_BAR_HTML = `
<div class="comment-textarea" contenteditable="true" spellcheck="false" style="height: 24px;"></div>
<a class="write-comment-btn" href="#">
    <img class="bar-icon" src="/images/diary/write-page/write_icon.svg"/>
</a>
<a class="sticker-btn" href="#">
    <img class="sticker-icon">
</a>
`;
const COMMENT_BAR_HTML = `
<div class="comment-bar">
    ${COMMENT_COMMENT_BAR_HTML}
</div>
`;

async function drawComment(characterColor, commentParent) {
    const comment = await createComment();
    const character = document.querySelector(`.comment-character.${characterColor}`);

    commentParent.appendChild(comment);
    processByCommentVertical(character, comment);
    processByCommentHorizontal(character, comment);
    comment.innerHTML += STICKER_BAR_HTML;

    if (!commentBtn.classList.contains("selected")) {
        adjustHeightByWrittenComment();
        adjustHeightByWrittenReplies();
    }

    addEventInCommentBox();
}

function addEventInCommentBox() {
    if (commentBtn.classList.contains("selected")) {
        document.querySelector(".comment-bar .write-comment-btn").addEventListener("click", clickWriteCommentBtn);
        document.addEventListener("click", clickWriteCommentOutside);
    } else {
        isActive = false;
        document.querySelector(".reply-bar .write-comment-btn").addEventListener("click", clickWriteReplyBtn);
        document.addEventListener("click", clickWrittenCommentOutside);
    }
    document.querySelector(".sticker-btn").addEventListener("click", clickStickerBtn);
    addEventToStickerIcons();
    addEventInCommentTextarea();
}

async function createComment() {
    const comment = document.createElement("div");

    comment.classList.add("comment");
    if (commentBtn.classList.contains("selected")) {
        comment.style.height = "70px";
    } else {
        comment.style.height = "125px";
    }
    comment.appendChild(await createCommentBox());
    return comment;
}

async function createCommentBox() {
    const commentBox = document.createElement("div");

    commentBox.classList.add("comment-box");
    if (commentBtn.classList.contains("selected")) {
        commentBox.style.height = "50px";
        commentBox.innerHTML = COMMENT_BAR_HTML;
    } else {
        commentBox.style.height = "100px";
        commentBox.innerHTML = await makeWrittenCommentBoxHTML();
    }
    return commentBox;
}

async function makeWrittenCommentBoxHTML() {
    const commentId = document.querySelector(".note-content .comment-character:not(.written)").classList[2];
    const comment = await getCommentById(commentId);
    const repliesHTML = makeRepliesHTML(comment.replies);
    const replyBarHTML = makeReplyBoxHTML(comment.profileImage)

    return `
    <div class="written-comment" style="height: 50px;">
        <div class="written-comment-text">${comment.content}</div>
    </div>
     <div class="reply-box" style="height: 44px;">
        ${repliesHTML}
        ${replyBarHTML}
    </div>
    `;
}

async function getCommentById(commentId) {
    return await fetch(`/api${currentPathName}/comments/${commentId}`)
        .then(response => {
            if (response.status === 200) {
                return response.json();
            } else {
                openNotificationModal("error", "댓글 조회 실패", 2000);
            }
        });
}

function makeRepliesHTML(replies) {
    var repliesHTML = "";

    if (replies.length === 0) {
        return "";
    }

    replies.forEach(reply => {
        repliesHTML += `
        <div class="reply">
            <div class="reply-character">
                <img class="reply-character-icon ${reply.profileImage}">
            </div>
            <div class="reply-text">${reply.content}</div>
        </div>
        `;
    })
    return `
    <div class="replies">
        ${repliesHTML}
    </div>
    <div class="reply-partition"></div>
    `;
}

function makeReplyBoxHTML(profileImage) {
    return `
    <div class="reply-bar">
        <div class="reply-character">
            <img class="reply-character-icon ${profileImage}">
        </div>
        ${COMMENT_COMMENT_BAR_HTML}
    </div>
    `;
}

function processByCommentVertical(character, comment) {
    if (character.offsetLeft <= STANDARD_LEFT) {
        comment.classList.add("left");
    }
    if (character.offsetLeft >= commentArea.offsetWidth - STANDARD_LEFT - character.offsetWidth) {
        comment.classList.add("right");
    }
}

function processByCommentHorizontal(character, comment) {
    const commentArrow = createCommentArrow(character, comment);
    var standard = STANDARD_TOP;

    if (!commentBtn.classList.contains("selected")) {
        standard -= 18;
    }
    if (character.offsetTop < standard) {
        comment.style.top = `${character.offsetTop + character.offsetHeight}px`;
        comment.classList.add("top");
        comment.prepend(commentArrow);
    } else {
        if (commentBtn.classList.contains("selected")) {
            comment.style.bottom = `${comment.parentElement.offsetHeight - character.offsetTop}px`;
        } else {
            comment.style.bottom = `${comment.parentElement.offsetHeight - character.offsetTop - 5}px`;
        }
        comment.classList.add("bottom");
        comment.appendChild(commentArrow);
    }
}

function createCommentArrow(character, comment) {
    const commentArrow = document.createElement("div");

    commentArrow.classList.add("comment-arrow");
    commentArrow.style.left = `${character.offsetLeft - comment.offsetLeft + 6}px`;
    return commentArrow;
}
