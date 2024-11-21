const content = document.querySelector(".content");
const bottom = document.querySelector(".bottom");
const pages = [];

init();

function init() {
    drawTodayDate();
    addEventToWriteBtn();
    makePages();
    addEventSlide();
}

function drawTodayDate() {
    const date = document.querySelector(".date")
    const today = new Date();

    date.innerText = `${today.getFullYear()}.` +
        `${today.getMonth() + 1 < 10 ? "0" + today.getMonth() + 1 : today.getMonth() + 1}.` +
        `${today.getDate() < 10 ? "0" + today.getDate(): today.getDate()}`;
}

function addEventToWriteBtn() {
    const writeBtn = document.querySelector(".write-btn");

    writeBtn.addEventListener("click", checkWrite);
}

async function checkWrite() {
    const result = await openConfirmModal(`일기를 업로드할까요?`, "일기가 업로드되면 수정 또는 삭제가 불가능해요.");

    if (result) {
        writeDiary();
    }
}

function writeDiary() {
    const formData = new FormData();
    const activeContents = Array.from(content.querySelectorAll(".active .diary-content"));
    const contents = activeContents.map(content => { return { content: content.value } });
    const json = JSON.stringify({
        contents: contents,
        todayMood: getTodayMood()
    });

    formData.append("data", new Blob([json], {type: "application/json"}));
    formData.append("file", getUploadImage());

    closeModal();
    fetch(`/api/groups/${groupId}/diaries`, {
        method: "post",
        body: formData
    })
        .then(response => {
            if (response.status !== 201) {
                throw response;
            }
            return response.headers.get("content-location");
        })
        .then(contentLocation => {
            openNotificationModal("success", ["일기가 작성되었어요!"], 2000, () => redirect(contentLocation));
        })
        .catch(async response => {
            if (response.status === 400 || response.status === 500) {
                throw await response.json();
            }
        })
        .catch(async data => {
            const messages = data.message.split("\n");
            openNotificationModal("error", messages, 2000);
        });
}

function getTodayMood() {
    const defaultMoodIconLocation = "/images/diary/write-page/mood_icon.svg";
    const mood = document.querySelector(".mood-btn").children[0];
    const moodLocation = mood.src.substring(mood.src.indexOf("/images"));

    if (moodLocation === defaultMoodIconLocation) {
        return null;
    }
    return moodLocation.substring(moodLocation.lastIndexOf("/"));
}

function getUploadImage() {
    const photo = document.querySelector("#photo-input");

    if (photo.files.length === 1) {
        return photo.files[0];
    }
    return null;
}

function makePages() {
    const noteContents = content.children;
    const pageCircles = bottom.children;
    for (var index = 0; index < 5; index++) {
        const page = {
            index: index,
            noteContent: noteContents[index],
            pageCircle: pageCircles[index],
            diaryContent: noteContents[index].querySelector(".diary-content")
        }
        pages.push(page);
    }
    currentPage = pages[0];
    nextPage = pages[1];
}

function addEventSlide() {
    pages.forEach(page => {
        addSlideEventByNoteContent(page.noteContent);
        page.noteContent.addEventListener("touchstart", () => page.diaryContent.blur());
    });
    content.addEventListener("transitionend", (event) => {
        if (event.target === currentPage.noteContent) {
            currentPage.diaryContent.focus();
        }
    });
}

function changePage(targetPage) {
    drawPages(targetPage);
    currentPage.pageCircle.classList.remove("current");
    currentPage = targetPage;
    prevPage = getPrevPage();
    nextPage = getNextPage();
    currentPage.pageCircle.classList.add("current");
}

function drawPages(targetPage) {
    if (currentPage.index < targetPage.index) {
        targetPage.noteContent.classList.add("active");
        targetPage.pageCircle.classList.add("active");
    }

    if (currentPage.index > targetPage.index) {
        if (isDeleted(currentPage)) {
            currentPage.noteContent.classList.remove("active");
            currentPage.pageCircle.classList.remove("active");
        }
    }
}

function isDeleted(page) {
    const isEmptyAfterPage = pages.slice(page.index + 1).every(page => page.diaryContent.value === "");
    if (isEmptyAfterPage) {
        return page.diaryContent.value === "";
    }
    return false;
}

function getNextPage() {
    if (currentPage.index === pages.length - 1) {
        return null
    }
    return pages[currentPage.index + 1];
}

function getPrevPage() {
    if (currentPage.index === 0) {
        return null
    }
    return pages[currentPage.index - 1];
}
