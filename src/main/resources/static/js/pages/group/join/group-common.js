import { drawStep1, confirmStep1 } from "./group-step1.js";
import { drawStep2, confirmStep2 } from "./group-step2.js";
import { drawStep3, confirmStep3 } from "./group-step3.js";
import { drawStep4, confirmStep4 } from "./group-step4.js";
import { drawStep5, confirmStep5 } from "./group-step5.js";

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

confirm_btn.addEventListener("click", confirmStep);
drawStep(currentStep, "stop");

function nextStep() {
    addStepIcon();
    removeStep(-1);
    drawStep(currentStep, "next");
}

function addStepIcon() {
    currentStep += 1;
    const stepIcon = stepIcons.find((stepIcon) => !stepIcon.classList.contains("fill"));
    stepIcon.classList.add("fill");
}

function removeStep(direction) {
    const step_content = note_body.children[0];
    step_content.style.transform = `translateX(${100 * direction}%)`;
    setTimeout(() => step_content.remove(), 300);
}

function drawStep(stepNumber, direction) {
    setTimeout(() => steps[stepNumber].draw(direction), 350);
}

function prevStep() {
    deleteStepIcon();
    removeStep(1);
    drawStep(currentStep, "prev");
}

function deleteStepIcon() {
    currentStep -= 1;
    const stepIcon = stepIcons.findLast((stepIcon) => stepIcon.classList.contains("fill"));
    stepIcon.classList.remove("fill");
}

async function confirmStep() {
    if (await steps[currentStep].confirm()) {
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

        backBtn.addEventListener("click", () => prevStep());
    }
}

function existBackBtn() {
    return document.querySelector(".top-bar .back-btn");
}
