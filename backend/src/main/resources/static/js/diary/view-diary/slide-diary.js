var currentPage = null;
var prevPage = null;
var nextPage = null;
var startX = 0;
var slideType = "stop";
var isActive = true;

function addSlideEventByNoteContent(noteContent) {
    noteContent.addEventListener("touchstart", slidePage);
    noteContent.addEventListener("touchmove", movePage);
    noteContent.addEventListener("touchend", () => changePageBySlide(slideType, "0.3s"));
}

function slidePage(event) {
    if (isActive) {
        startX = event.touches[0].pageX;
        slideType = "stop";
        changeTransformTime(prevPage, "0s");
        changeTransformTime(nextPage, "0s");
        changeTransformTime(currentPage, "0s");
    }
}

function movePage(event) {
    if (isActive) {
        const changedX = -(startX - event.touches[0].pageX);

        changeTransformX(prevPage, -window.innerWidth + changedX);
        changeTransformX(nextPage, window.innerWidth + changedX);
        changeTransformX(currentPage, changedX);

        slideType = "stop";
        if (changedX <= -100 && nextPage !== null) {
            slideType = "next";
        }
        if (changedX >= 100 && prevPage !== null) {
            slideType = "prev";
        }
    }
}

function changePageBySlide(slideType, time) {
    if (isActive) {
        changePage(getPageBySlideType(slideType));
        changeTransform(prevPage, time, "-100%");
        changeTransform(nextPage, time, "100%");
        changeTransform(currentPage, time, "0%");
    }
}

function changeTransformTime(page, time) {
    if (page !== null) {
        page.noteContent.style.transition = `transform ${time} ease-out`;
    }
}

function changeTransformX(page, changedX) {
    if (page !== null) {
        page.noteContent.style.transform = `translateX(${changedX}px)`;
    }
}

function changeTransform(page, time, changedX) {
    if (page !== null) {
        page.noteContent.style.transition = `transform ${time} ease-out`;
        page.noteContent.style.transform = `translateX(${changedX})`;
    }
}

function getPageBySlideType(type) {
    if (type === "stop") {
        return currentPage;
    }
    if (type === "next") {
        return nextPage;
    }
    return prevPage;
}
