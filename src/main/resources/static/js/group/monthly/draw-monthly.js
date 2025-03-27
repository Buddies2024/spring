class Calendar {
    constructor(firstDay, today) {
        this.firstDay = firstDay;
        this.today = today;
        this.$year = document.querySelector(".year");
        this.$month = document.querySelector(".month");
        this.$weeks = Array.from(document.querySelectorAll("table tr")).slice(3);
        this.$days = [];
        this.draw(today.getFullYear(), today.getMonth() + 1);
    }

    draw(year, month) {
        this.drawYear(year);
        this.drawMonth(month);
        const firstWeekday = new Date(year, month - 1, 1).getDay();
        const lastDay = new Date(year, month, 0).getDate();
        this.drawDays(firstWeekday, lastDay);
        this.drawToday();
    }

    drawYear(year) {
        this.year = year;
        this.$year.innerText = year;
    }

    drawMonth(month) {
        this.month = month;
        this.$month.innerText = month;
    }

    drawDays(weekday, lastDay) {
        this.clearDays();
        this.$days = [];
        let week = 0;

        for (let day = 0; day < lastDay; day++) {
            const $day = this.$weeks[week].children[weekday];
            $day.innerHTML = `<span class="day">${day + 1}</span>`;
            this.$days.push($day);

            weekday++;
            if (weekday === 7) {
                weekday = 0;
                week++;
            }
        }
    }

    drawDay(day, html) {
        const $day = this.getDay(day);
        $day.innerHTML = html;
    }

    getDay(day) {
        return this.$days[day - 1];
    }

    drawToday() {
        if (this.hasToday()) {
            const day = this.today.getDate();
            this.getDay(day).classList.add("today");
        }
    }

    hasToday() {
        if (today.getFullYear() !== this.year) {
            return false
        }
        if (today.getMonth() !== this.month - 1) {
            return false
        }
        return true
    }

    clearDays() {
        this.$days.forEach($day => {
            $day.innerHTML = "";
            $day.className = "";
        });
    }

    nextMonth() {
        let targetMonth = this.month + 1;
        if (targetMonth > 12) {
            this.year += 1;
            targetMonth = 1;
        }

        this.draw(this.year, targetMonth);
    }

    prevMonth() {
        let targetMonth = this.month - 1;
        if (targetMonth < 1) {
            this.year -= 1;
            targetMonth = 12;
        }
        this.draw(this.year, targetMonth);
    }

    canMoveNext() {
        if (this.year < this.today.getFullYear()) {
            return true;
        }
        return this.year === this.today.getFullYear() && this.month - 1 < this.today.getMonth();
    }

    canMovePrev() {
        if (this.year > this.firstDay.getFullYear()) {
            return true;
        }
        return this.year === this.firstDay.getFullYear() && this.month - 1 > this.today.getMonth();
    }

    reload() {
        this.draw(this.year, this.month);
        drawWrittenDays();
        drawBottom();
    }
}

const firstDay = new Date(groupCreatedYear, groupCreatedMonth);
const today = new Date();
const calendar = new Calendar(firstDay, today);

function init() {
    const groupName = document.querySelector(".group-name");
    const leftArrow = document.querySelector(".left-arrow");
    const rightArrow = document.querySelector(".right-arrow");
    
    groupName.addEventListener("click", () => location.reload());
    leftArrow.addEventListener("click", clickLeftArrowButton);
    rightArrow.addEventListener("click", clickRightArrowButton);

    drawWrittenDays();
    drawBottom();
}

function clickLeftArrowButton() {
    if (calendar.canMovePrev()) {
        calendar.prevMonth();
        drawWrittenDays();
        drawBottom();
    }
}

function clickRightArrowButton() {
    if (calendar.canMoveNext()) {
        calendar.nextMonth();
        drawWrittenDays();
        drawBottom();
    }
}

async function drawWrittenDays() {
    const writtenDays = await fetch(`/api/groups/${groupId}/diaries/monthly?year=${calendar.year}&month=${calendar.month}`)
    .then(response => response.json())
    .then(data => data.days) ?? location.reload();

    writtenDays.forEach(writtenDay => {
        const html = makeDiaryHTML(writtenDay);
        calendar.drawDay(writtenDay.day, html);
    })
}

function makeDiaryHTML(writtenDay) {
    const profileHTML = makeProfileHTML(writtenDay.profileImage);
    const url = `/groups/${groupId}/diaries/${writtenDay.id}`;
    let htmlClass = "day";
    if (!writtenDay.canView) {
        htmlClass += " cannot-view gray";
    }
    return `<a class="${htmlClass}" href="${url}">${profileHTML}</a>`;
}

function makeProfileHTML(profileImage) {
    let htmlClass = `${profileImage} profile-icon`;
    if (profileImage === "blue" || profileImage === "green") {
        htmlClass += ` ${profileImage}-icon`
    }
    return `<img class="${htmlClass}">`
}

function drawBottom() {
    const calendarBottom = document.querySelector(".calendar-bottom")

    fetch(`/api/groups/${groupId}/diaries/status`)
        .then(response => response.json())
        .then(data => {
            calendarBottom.innerHTML = getCalendarBottomHtml(data);
            if (calendar.hasToday() && data.isMyOrder && !data.writtenTodayDiary) {
                const day = calendar.today.getDate();
                const html = `<a class="day" href="/groups/${groupId}/diaries">${day}</a>`;
                calendar.drawDay(day, html);
            }
        });
}

function getCalendarBottomHtml(diaryStatus) {
    if (diaryStatus.viewableDiaryId != null) {
        return `<a href="/groups/${groupId}/diaries/${diaryStatus.viewableDiaryId}" class="bottom-font">
                        <span class="font-bold">오늘 일기가 업로드 되었어요.</span><br>
                        <span>날짜를 눌러 확인해보세요!</span>
                    </a>`;
    }
    if (diaryStatus.isMyOrder) {
        return `<a href="/groups/${groupId}/diaries" class="bottom-font">
                    <span>내가 일기를 작성할 차례에요.</span><br>
                    <span>기다리는 친구들을 위해</span><br>
                    <span class="font-bold">일기를 작성해주세요!</span>
                </a>`;
    }
    if (!diaryStatus.writtenTodayDiary) {
        return `<div class="bottom-font">
                    <span class="font-bold">아직 친구가 일기를 작성하지 않았어요!</span>
                </div>`;
    }
    return `<div class="bottom-font">
                <span class="font-bold">친구가 일기가 업로드 했어요.</span><br>
                <span>내 차례가 올 때까지 일기를 기다려요!</span> 
            </div>`;
}

init();
