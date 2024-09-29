const modal = document.querySelector("#modal");
const modalBody = modal.querySelector(".modal-body");
const moodBtn = document.querySelector(".mood-btn");
const photoBtn = document.querySelector(".photo-btn");

var currentModal = null

moodBtn.addEventListener("click", (event) => clickModalBtn(event, makeEmojiHtml()));
photoBtn.addEventListener("click", (event) => clickModalBtn(event, makePhotoHtml()));

function clickModalBtn(event, htmlString) {
    if (modal.style.display === "block") {
        closeModal();

        if (event.target != currentModal && currentModal != null) {
            setTimeout(() => openModal(htmlString), 300);
            currentModal = event.target;
        }
    } else {
        openModal(htmlString);
        currentModal = event.target;
    }
}

function closeModal() {
    modal.style.transform = "translateY(100%)";
    setTimeout(() => modal.style.display = "none", 300);
}

function openModal(htmlString="") {
    modal.style.display = "block";
    setTimeout(() => modal.style.transform = "translateY(0)", 10);

    drawBody(htmlString);
}

function drawBody(htmlString) {
    modalBody.innerHTML = htmlString;
}

function makeEmojiHtml() {
    return `<table>
                <tr>
                    <td><a href="#"><img src="/images/write-page/emoji/hilarious.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/wink.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/soso.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/calm.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/surprise.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/panic.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                </tr>
                <tr>
                    <td><a href="#"><img src="/images/write-page/emoji/dislike.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/dizzy.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/happy.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/bigsmile.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/hate.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/vexation.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                </tr>
                <tr>
                    <td><a href="#"><img src="/images/write-page/emoji/sunglasses.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/funny.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/smirk.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/beam.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/disgust.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                    <td><a href="#"><img src="/images/write-page/emoji/sleepy.svg" style="width: 26px; height: 26px; flex-shrink: 0;"/></a></td>
                </tr>
            </table>`
}

function makePhotoHtml() {
    return `<div style="width: 50px; height: 50px; position: relative; top: 17px; left: 44px; float: left;">
                <a href="#"><img src="/images/write-page/modal/camera_icon.svg" style="width: 50px; height: 50px; flex-shrink: 0;"/></a>
            </div>
            <div style="width: 2px; height: 100px; position: relative; top: 17px; left: 103px; background-color: #767676; float: left;"></div>
            <div style="width: 48px; height: 48px; position: relative; top: 17px; left: 164px; float: left;">
                <a href="#"><img src="/images/write-page/modal/photo_icon.svg" style="width: 48px; height: 48px; flex-shrink: 0;"/></a>
            </div>`
}