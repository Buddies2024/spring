const leftArrow = document.querySelector(".left-arrow");
const rightArrow = document.querySelector(".right-arrow");

leftArrow.addEventListener("click", clickLeftArrowButton);
rightArrow.addEventListener("click", clickRightArrowButton);

function clickLeftArrowButton(event) {
    event.preventDefault();
    calendar.prevMonth();
}

function clickRightArrowButton(event) {
    event.preventDefault();
    calendar.nextMonth();
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
