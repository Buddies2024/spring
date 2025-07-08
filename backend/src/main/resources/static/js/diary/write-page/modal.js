const modal = document.querySelector("#modal");
const modalBody = modal.querySelector(".modal-body");
const moodBtn = document.querySelector(".mood-btn");

var currentModal = null

modal.style.display = "none";

backgroundImage = new Image();
backgroundImage.addEventListener("load", () => modal.style.backgroundImage = `url('${backgroundImage.src}')`)
backgroundImage.src = "/images/diary/write-page/modal/background.svg";

moodBtn.addEventListener("click", (event) => {
    clickModalBtn(event);
});

Array.from(modalBody.querySelectorAll("a")).forEach(mood => {
    mood.addEventListener("click", changeMood);
});

function clickModalBtn(event) {
    if (modal.style.display === "block") {
        closeModal();

        if (event.target !== currentModal && currentModal != null) {
            setTimeout(() => openModal(), 300);
            currentModal = event.target;
        }
    } else {
        openModal();
        currentModal = event.target;
    }
}

function closeModal() {
    modal.style.transform = "translateY(100%)";
    setTimeout(() => {
        modal.style.display = "none";
    }, 300);
}

function openModal() {
    modal.style.display = "block";
    setTimeout(() => modal.style.transform = "translateY(0)", 10);
}

function changeMood(event) {
    const moodBtn = document.querySelector(".mood-btn");
    moodBtn.children[0].src = event.target.src;
    moodBtn.children[0].classList.add("mood-icon");
    closeModal();
}
