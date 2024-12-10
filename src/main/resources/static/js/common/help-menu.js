const helpIcon = document.querySelector(".help-icon");
const tooltip = document.querySelector(".tooltip");
const helpButton = document.querySelector(".help-btn");
const helpImageContainer = document.querySelector(".help-image-container");
const helpImage = document.querySelector(".help-image");
const inquiryButton = document.querySelector(".inquiry-btn");
const inquiryPopupContainer = document.querySelector(".inquiry-popup-container");
const overlay = document.querySelector(".overlay");

let helpImagePath = '';

helpIcon.addEventListener('click', () => {
    tooltip.classList.toggle('hidden');
});

document.addEventListener('click', (event) => {
    if (!helpIcon.contains(event.target) && !tooltip.contains(event.target)) {
        tooltip.classList.add('hidden');
    }
});

helpButton.addEventListener('click', () => {
    const pathname = window.location.pathname;
    const stepContent = document.querySelector(".step-content");

    const monthlyRegexp = /^\/groups\/[a-zA-Z0-9]+$/;
    const diaryWriteRegexp = /^\/groups\/[a-zA-Z0-9]+\/diaries$/;
    const diaryViewRegexp = /^\/groups\/[a-zA-Z0-9]+\/diaries\/\d+$/;

    if (monthlyRegexp.test(pathname)) {
        showHelpMonthly();
    } else if (diaryWriteRegexp.test(pathname)) {
        helpImagePath = '/images/common/help-menu/help-image/help-diary-write.svg';
    } else if (diaryViewRegexp.test(pathname)) {
        helpImagePath = '/images/common/help-menu/help-image/help-diary-view.svg';
    } else if (stepContent.classList.contains("step1")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-step1.svg';
    } else if (stepContent.classList.contains("step2-create")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-create-step2.svg';
    } else if (stepContent.classList.contains("step2-join")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-join-step2.svg';
    } else if (stepContent.classList.contains("step3")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-step3.svg';
    } else if (stepContent.classList.contains("step4")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-step4.svg';
    } else if (stepContent.classList.contains("step5-create")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-create-step5.svg';
    } else if (stepContent.classList.contains("step5-join")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-join-step5.svg';
    }
    showHelpImage(helpImagePath);
});

function showHelpMonthly() {
    if (groupMenu.classList.contains("blur")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-menu.svg';
    } else {
        helpImagePath = '/images/common/help-menu/help-image/help-monthly.svg';
    }
}

function showHelpImage(helpImagePath) {
    helpImage.src = helpImagePath;
    helpImageContainer.classList.remove('hidden');
    tooltip.classList.add('hidden');
}

helpImageContainer.addEventListener('click', () => {
    helpImageContainer.classList.add('hidden');
});

inquiryButton.addEventListener('click', () => {
    inquiryPopupContainer.classList.remove('hidden');
    overlay.classList.remove("hidden");
    tooltip.classList.add('hidden');
});

overlay.addEventListener("click", () => {
    inquiryPopupContainer.classList.add("hidden");
    overlay.classList.add("hidden");
});