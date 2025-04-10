const note_body = document.querySelector(".note-body");
const step_bar = document.querySelector(".step-bar");
const stepIcons = Array.from(step_bar.children);
const confirm_btn = document.querySelector(".confirm-btn");
const steps = {
    1: {
        "draw": drawStep1,
        "confirm": confirmStep1
    },
    2: {
        "draw": drawStep2,
        "confirm": confirmStep2
    },
    3: {
        "draw": drawStep3,
        "confirm": confirmStep3
    },
    4: {
        "draw": drawStep4,
        "confirm": confirmStep4
    },
    5: {
        "draw": drawStep5,
        "confirm": confirmStep5
    }
}
const groupData = {
    "groupId": "",
    "groupName": "",
    "profileImage": "",
    "nickname": "",
    "groupCode": ""
}

var currentStep = 1;
var isMoving = false;

confirm_btn.addEventListener("click", confirmStep);
drawStep(currentStep, "stop");

function nextStep() {
    isMoving = true;
    addStepIcon();
    removeStep(-1, () => drawStep(currentStep, "next"));
}

function addStepIcon() {
    currentStep += 1;
    const stepIcon = stepIcons.find((stepIcon) => !stepIcon.classList.contains("fill"));
    stepIcon.classList.add("fill");
}

function removeStep(direction, callback) {
    const step_content = note_body.children[0];
    step_content.style.transform = `translateX(${100 * direction}%)`;

    step_content.addEventListener("transitionend", () => { 
        step_content.remove();
        if (callback) callback();
    });
}

function drawStep(stepNumber, direction) {
    steps[stepNumber].draw(direction);
    isMoving = false;
}

function prevStep() {
    if (currentStep > 1) {
        isMoving = true;
        deleteStepIcon();
        removeStep(1, () => drawStep(currentStep, "prev"));
    }
    
}

function deleteStepIcon() {
    currentStep -= 1;
    const stepIcon = stepIcons.findLast((stepIcon) => stepIcon.classList.contains("fill"));
    stepIcon.classList.remove("fill");
}

async function confirmStep() {
    if (!isMoving && await steps[currentStep].confirm()) {
        nextStep();
    }
}

function removeBackBtn() {
    if (existBackBtn()) {
        const backBtn = document.querySelector(".bar-back");

        backBtn.remove();
    }
}

function addBackBtn() {
    if (!existBackBtn()) {
        const topBar = document.querySelector(".top-bar");
        const backBtn = document.createElement("div");

        backBtn.classList.add("bar-back");
        backBtn.innerHTML = `<a href="#" class="back-btn"><img class="back-icon" src="/images/common/back_icon.svg"/></a>`;
        topBar.appendChild(backBtn);

        backBtn.addEventListener("click", () => !isMoving && prevStep());
    }
}

function existBackBtn() {
    return document.querySelector(".top-bar .back-btn");
}
