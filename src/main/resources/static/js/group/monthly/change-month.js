const leftArrow = document.querySelector(".left-arrow");
const rightArrow = document.querySelector(".right-arrow");

leftArrow.addEventListener("click", clickLeftArrowButton);
rightArrow.addEventListener("click", clickRightArrowButton);

function clickLeftArrowButton(event) {
    event.preventDefault();
    if (!isSameGroupCreatedYearAndMonth()) {
        month.innerText -= 1;
        if (month.innerText === "0") {
            month.innerText = 12;
            year.innerText -= 1;
        }
        clearDate();
        drawDateOfCalendar();
    }
}

function clickRightArrowButton(event) {
    event.preventDefault();
    if (!isSameCurrentDateYearAndMonth()) {
        month.innerText = Number(month.innerText) + 1;
        if (month.innerText === "13") {
            month.innerText = 1;
            year.innerText = Number(year.innerText) + 1;
        }
        clearDate();
        drawDateOfCalendar();
    }
}

function isSameGroupCreatedYearAndMonth() {
    return Number(year.innerText) === groupCreatedYear
        && Number(month.innerText) === groupCreatedMonth;
}

function isSameCurrentDateYearAndMonth() {
    const today = new Date();

    return Number(year.innerText) === today.getFullYear()
        && Number(month.innerText - 1) === today.getMonth();
}
