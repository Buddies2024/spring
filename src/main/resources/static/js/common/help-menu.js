const helpIcon = document.querySelector(".help-icon");
const tooltip = document.querySelector(".tooltip");
const helpButton = document.querySelector(".help-btn");
const helpImageContainer = document.querySelector(".help-image-container");
const helpImage = document.querySelector(".help-image");
const inquiryButton = document.querySelector(".inquiry-btn");
const inquiryPopupContainer = document.querySelector(".inquiry-popup-container");
const overlay = document.querySelector(".overlay");

let helpImagePath = '';
let currentImageIndex = 0;
const diaryViewImages = [
    '/images/common/help-menu/help-image/help-diary-view.png',
    '/images/common/help-menu/help-image/help-diary-view-new-comment.png',
    '/images/common/help-menu/help-image/help-diary-view-comment-write.png',
    '/images/common/help-menu/help-image/help-diary-view-reply.png'
];

if (window.location.pathname === '/' || window.location.pathname === '/login') {
    document.querySelector('.help-icon-container').remove();
}

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
        helpImagePath = '/images/common/help-menu/help-image/help-diary-write.png';
    } else if (diaryViewRegexp.test(pathname)) {
        helpImagePath = diaryViewImages[currentImageIndex];
        currentImageIndex++;
    } else if (stepContent.classList.contains("step1")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-step1.png';
        helpImagePath = '/images/common/help-menu/help-image/help-group-step1.png';
    } else if (stepContent.classList.contains("step2-create")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-create-step2.png';
    } else if (stepContent.classList.contains("step2-join")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-join-step2.png';
    } else if (stepContent.classList.contains("step3")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-step3.png';
    } else if (stepContent.classList.contains("step4")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-step4.png';
    } else if (stepContent.classList.contains("step5-create")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-create-step5.png';
    } else if (stepContent.classList.contains("step5-join")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-join-step5.png';
    }
    showHelpImage(helpImagePath);
});

function showHelpMonthly() {
    if (groupMenu.classList.contains("blur")) {
        helpImagePath = '/images/common/help-menu/help-image/help-group-menu.png';
    } else {
        helpImagePath = '/images/common/help-menu/help-image/help-monthly.png';
    }
}

function showHelpImage(helpImagePath) {
    helpImage.src = helpImagePath;
    helpImageContainer.classList.remove('hidden');
    tooltip.classList.add('hidden');
}

helpImageContainer.addEventListener('click', () => {
    if (currentImageIndex >= diaryViewImages.length) {
        currentImageIndex = 0;
    }
    if (currentImageIndex !== 0) {
        showHelpImage(diaryViewImages[currentImageIndex])
        currentImageIndex++;
    }
    else {
        helpImageContainer.classList.add('hidden');
    }
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
