class Calendar {
    constructor(today) {
        this.today = today;
        this.$year = document.querySelector(".year");
        this.$month = document.querySelector(".month");
        this.$weeks = Array.from(document.querySelectorAll("table tr")).slice(3);
        this.$days = [];
        this.setup(today.getFullYear(), today.getMonth() + 1);
    }

    setup(year, month) {
        this.drawYear(year);
        this.drawMonth(month);
        const firstWeekday = new Date(year, month - 1, 1).getDay();
        const lastDay = new Date(year, month, 0).getDate();
        this.drawDays(firstWeekday, lastDay);
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

    clearDays() {
        this.$days.forEach($day => $day.innerHTML = "");
    }

    nextMonth() {
        let targetMonth = this.month + 1;
        if (targetMonth > 12) {
            this.year += 1;
            targetMonth = 1;
        }

        this.setup(this.year, targetMonth);
    }

    prevMonth() {
        let targetMonth = this.month - 1;
        if (targetMonth < 1) {
            this.year -= 1;
            targetMonth = 12;
        }
        this.setup(this.year, targetMonth);
    }
}

const today = new Date();
const calendar = new Calendar(today);

function init() {
    const groupName = document.querySelector(".group-name");
    
    // drawDateOfCalendar();
    drawBottom();

    groupName.addEventListener("click", () => location.reload());
}

function drawBottom() {
    const calendarBottom = document.querySelector(".calendar-bottom")

    fetch(`/api/groups/${groupId}/diaries/status`)
        .then(response => response.json())
        .then(data => {
            calendarBottom.innerHTML = getCalendarBottomHtml(data);
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

// function drawDateOfCalendar() {
//     const firstDay = new Date(year.innerText, month.innerText - 1, 1).getDay();
//     const lastDate = new Date(year.innerText, month.innerText, 0).getDate();
    

//     let date = 1;
//     let day = firstDay
//     let column = 0;

//     while (date <= lastDate) {
//         trs[column].children[day].innerHTML = `<span class="date day${date}">${date}</span>`;
//         date++;
//         day++;
//         if (day === 7) {
//             day = 0;
//             column++;
//         }
//     }
//     // drawWrittenDays();
//     changeGrayProfile(writtenDays);
//     addBorderToday();
//     removeClickToday();
// }

// // async function drawWrittenDays() {
// //     const writtenDays = await fetch(`/api/groups/${groupId}/diaries/monthly?year=${year.innerText}&month=${month.innerText}`)
// //     .then(response => response.json())
// //     .then(data => data.days);

// //     writtenDays.forEach(writtenDay => {
// //         const day = 
// //     })
// // }

// function makeCircle(date, writtenDiaryDays) {
//     const index = writtenDiaryDays.findIndex((day) => day.day === date);
//     if (index !== -1) {
//         return getProfileImageHtml(writtenDiaryDays[index], date);
//     }
//     if (isToday(date)) {
//         return `<a class="date day${date} highlight" href="/groups/${groupId}/diaries">${date}</a>`;
//     }
//     return ;
// }

// function getProfileImageHtml(diary, date) {
//     const profileImage = diary.profileImage;
//     const diaryId = diary.id;

//     if (profileImage === "blue" || profileImage === "green") {
//         return `<a class="date day${date} highlight written" href="/groups/${groupId}/diaries/${diaryId}">
//                     <img class="${profileImage} profile-icon ${profileImage}-icon">
//                 </a>`;
//     }
//     return `<a class="date day${date} highlight written" href="/groups/${groupId}/diaries/${diaryId}">
//                 <img class="${profileImage} profile-icon">
//             </a>`;
// }

// function isToday(date) {
//     if (today.getFullYear() !== Number(year.innerText)) {
//         return false
//     }
//     if (today.getMonth() !== Number(month.innerText) - 1) {
//         return false
//     }
//     return today.getDate() === date;
// }

// function changeGrayProfile(days) {
//     days.forEach(day => {
//        if (!day.canView) {
//            console.log(day);
//            const dayBtn = document.querySelector(`.day${day.day}`);

//            dayBtn.classList.add("cannot-view");
//            dayBtn.children[0].classList.add("gray");
//        }
//     });
// }

// function addBorderToday() {
//     if (isToday(today.getDate())) {
//         const firstDay = new Date(today.getFullYear(), today.getMonth(), 1).getDay() - 1;
//         const todayDate = today.getDate();
//         const column = Math.floor((todayDate + firstDay) / 7);
//         const row = (todayDate + firstDay) % 7;
//         trs[column].children[row].querySelector("a").classList.add("today");
//     }
// }

// function removeClickToday() {
//     const today = document.querySelector(".today");

//     if (today && !canWrite) {
//         today.classList.add("cannot-view");
//     }
// }

init();
