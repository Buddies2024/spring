init();

function init() {
    drawTodayDate();

    addEventToTextArea();
    addEventToWriteBtn();
}

function addEventToTextArea() {
    const textArea = document.querySelector("textarea");

    textArea.addEventListener("click", closeModal);
}

function addEventToWriteBtn() {
    const writeBtn = document.querySelector(".write-btn")

    writeBtn.addEventListener("click", writeDiary);
}

function drawTodayDate() {
    const date = document.querySelector(".date")
    const today = new Date();

    date.innerText = `${today.getFullYear()}.` +
        `${today.getMonth() + 1 < 10 ? "0" + today.getMonth() + 1 : today.getMonth() + 1}.` +
        `${today.getDate() < 10 ? "0" + today.getDate(): today.getDate()}`;
}

function writeDiary() {
    var formData = new FormData();
    const json = JSON.stringify({
        content: document.querySelector(".diary-content").value,
        moodLocation: getMoodLocation()
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

function getMoodLocation() {
    const moodIconLocation = "/images/diary/write-page/mood_icon.svg";
    const mood = document.querySelector(".mood-btn").children[0];
    const moodLocation = mood.src.substring(mood.src.indexOf("/images"));

    if (moodLocation === moodIconLocation) {
        return null;
    }
    return moodLocation;
}

function getUploadImage() {
    const photo = document.querySelector("#photo-input");

    if (photo.files.length === 1) {
        return photo.files[0];
    }
    return null;
}
