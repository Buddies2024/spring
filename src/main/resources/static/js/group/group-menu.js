import { setFCMToken } from "/js/fcm/setup-fcm.js"

const BELL_ICONS = [
    "/images/group/menu/notification.svg",
    "/images/group/menu/notification-mute.svg"
];

preLoadImgage(BELL_ICONS);

const menuBtn = document.querySelector(".menu-btn");
const groupMenu = document.querySelector(".group-menu");
const menu = groupMenu.querySelector(".menu");
const groupMembers = menu.querySelector(".group-members");
const groupSize = menu.querySelector(".group-size .size");
const groupLeaveBtn = menu.querySelector(".group-leave");
const groupCodeBtn = menu.querySelector(".group-code");
const notificationBtn = menu.querySelector(".notification");
var isLeader = false;

menuBtn.addEventListener("click", openMenu);
groupMenu.addEventListener("click", closeMenu);
groupLeaveBtn.addEventListener("click", leaveGroup);
groupCodeBtn.addEventListener("click", () => {
    try {
        navigator.clipboard.writeText(groupCodeBtn.getAttribute("data-code"))
        openNotificationModal("success", ["복사 성공!", "친구들을 초대해 보아요."], 1000);
    } catch {
        openNotificationModal("error", ["오류가 발생했습니다."], 2000);
    }
});

function openMenu() {
    fetch(`/api/groups/${groupId}/members`)
        .then(response => response.json())
        .then(data => drawMenu(data));
    groupMenu.style.display = "block";
    groupMenu.classList.add("blur");
    setTimeout(() => menu.style.transform = "translateX(0)", 10);
    drawNotificationBtn();
    document.addEventListener("visibilitychange", reloadNotificationBtn);
}

function closeMenu(event) {
    if (event.target === groupMenu) {
        menu.style.transform = "translateX(100%)"
        groupMenu.classList.remove("blur");
        setTimeout(() => groupMenu.style.display = "none", 300);
        removeMembers();
        document.removeEventListener("visibilitychange", reloadNotificationBtn);
    }
}

function drawMenu(data) {
    groupSize.innerText = data.members.length;
    drawMembers(data.members);
    const members = groupMembers.querySelectorAll(".group-member");
    isLeader = false;

    members[data.selfIndex].innerHTML = makeMyHtml() + members[data.selfIndex].innerHTML;
    members[data.leaderIndex].querySelector(".profile-image").innerHTML += makeLeaderHtml();
    members[data.currentWriterIndex].classList.add("order");
    if (data.selfIndex === data.leaderIndex) {
        isLeader = true;
        groupMembers.classList.add("leader");
        members.forEach(member => member.addEventListener("click", selectGroupMember));
    }

    if (data.members.length === 1) {
        groupLeaveBtn.removeEventListener("click", leaveGroup);
        groupLeaveBtn.addEventListener("click", deleteGroup);
    } else {
        groupLeaveBtn.removeEventListener("click", deleteGroup);
        groupLeaveBtn.addEventListener("click", leaveGroup);
    }
}

function drawMembers(members) {
    const centerX = 88;
    const centerY = 119;
    const r = 79;
    const memberSize = members.length;
    var index = 0;

    members.forEach(member => {
        const groupMember = document.createElement("div");
        const radian = getRadian(getAngle(index, memberSize));
        groupMember.classList.add("group-member");
        groupMember.innerHTML = makeMemberHtml(member.profileImage, member.nickname);
        groupMember.style.left = `${centerX - r * Math.cos(radian)}px`;
        groupMember.style.top = `${centerY - r * Math.sin(radian)}px`;
        groupMembers.appendChild(groupMember);
        index++;
    });
}

function getRadian(angle) {
    return angle * Math.PI / 180;
}

function getAngle(number, memberSize) {
    const angle = 270 / (memberSize - 1) * number;
    if (isFinite(angle)) {
        return angle;
    }
    return 0;
}

function makeMemberHtml(characterName, memberName) {
    return `<a class="profile-image" href="javascript:void(0);">
                <img class="${characterName} character-icon" />
            </a>
            <span class="profile-nickname">${memberName}</span>`
}

function makeMyHtml() {
    return `<div class="my">
                <span style='color: #FFF; text-align: center; font-family: "HancomMalangMalang-Regular"; font-size: 6px; font-style: normal; font-weight: 700; line-height: 100%; letter-spacing: 0.06px;'>나</span>
            </div>`
}

function makeLeaderHtml() {
    return '<img class="crown" />'
}

function removeMembers() {
    const members = groupMembers.querySelectorAll(".group-member");
    Array.from(members).forEach(member => {
        member.remove();
    })
}

function leaveGroup(event) {
    event.preventDefault();
    if (!isLeader) {
        const url = event.target.closest("a").href;
        leaveGroupByMember(url);
    } else {
        openNotificationModal("error", ["방장은 탈퇴할 수 없습니다.", "방장 권한을 넘기고 탈퇴해주세요."], 2000);
    }
}

async function leaveGroupByMember(url) {
    const result = await openConfirmModal("정말 탈퇴하시겠어요?", "탈퇴할 시 모든 데이터가 영구적으로 삭제됩니다.");

    if (result) {
        fetch(url, {
            method: "PATCH"
        })
        .then(response => {
            if (response.status === 200) {
                openNotificationModal("success", ["탈퇴를 완료했어요.", "새로운 스프링을 시작해 보아요!"], 2000, () => window.location.href = '/groups');
            } else {
                openNotificationModal("error", ["오류가 발생했습니다."], 2000);
            }
        })
    }
}

async function deleteGroup(event) {
    event.preventDefault();
    const result = await openConfirmModal("정말 삭제하시겠어요?", "삭제할 시 모든 데이터가 영구적으로 삭제됩니다.");
    const url = event.target.closest("a").href;

    if (result) {
        fetch(url, {
            method: "PATCH"
        })
        .then(response => {
            if (response.status === 200) {
                openNotificationModal("success", ["삭제를 완료했어요.", "새로운 스프링을 시작해 보아요!"], 2000, () => window.location.href = '/groups');
            } else {
                openNotificationModal("error", ["오류가 발생했습니다."], 2000);
            }
        })
    }
}

function reloadNotificationBtn() {
    if (document.visibilityState === "visible" && !/^\/groups\/[A-Za-z0-9]{8}\/diaries$/.test(location.pathname)) {
        drawNotificationBtn();
    }
}

async function drawNotificationBtn() {
    const permission = Notification.permission;
    const classList = notificationBtn.classList;
    notificationBtn.removeEventListener("click", showNotificationSetting);
    notificationBtn.removeEventListener("click", changeNotificationState);
    classList.replace(classList[2], "denied");

    if (permission === "granted") {
        notificationBtn.innerHTML = "알림 활성화 중";
        await setFCMToken();
        drawNotificationToggleBtn(notificationBtn);
    } else {
        notificationBtn.innerHTML = "알림 권한 활성화";
        notificationBtn.addEventListener("click", showNotificationSetting);
    }
}

function showNotificationSetting() {
    const mobileOS = getMobileOS();
    var messages = [];
    if (mobileOS === "iOS") {
        messages = ["알림 권한이 꺼져 있어요.", "'설정 -> 앱 -> 스프링 -> 알림' 에서", "알림 권한을 허용 해주세요..!"];
    }
    if (mobileOS === "Android") {
        messages = ["알림 권한이 꺼져 있어요.", "'크롬->설정->알림->앱 알림 상세설정' 에서", "차단된 알림 권한을 허용 해주세요..!"];
    }
    openNotificationModal("error", messages, 2147483647);
}

async function drawNotificationToggleBtn(notificationBtn) {
    const classList = notificationBtn.classList;
    fetch("/api/member/notification")
    .then(response => {
        if (response.status === 200) {
            return response.json();
        }
        openNotificationModal("error", ["오류가 발생했습니다."], 2000);
    })
    .then(data => {
        if (data.onNotification) {
            classList.replace(classList[2], "on");
            notificationBtn.innerHTML = "<img />알림 ON";
        } else {
            classList.replace(classList[2], "off");
            notificationBtn.innerHTML = "<img />알림 OFF";
        }
        notificationBtn.addEventListener("click", changeNotificationState);
    });
}

function changeNotificationState(event) {
    const notificationBtn = event.target.closest("a.notification");

    if (notificationBtn.classList.contains("on")) {
        notificationBtn.classList.replace("on", "off");
        notificationBtn.innerHTML = "<img />알림 OFF";
    } else {
        notificationBtn.classList.replace("off", "on");
        notificationBtn.innerHTML = "<img />알림 ON";
    }
    notificationBtn.removeEventListener("click", changeNotificationState);
    requestToggleNotification(notificationBtn);
}

function requestToggleNotification(notificationBtn) {
    const url = "/api/member/notification"
    fetch(url, {
        method: "PATCH"
    })
    .then(response => {
        if (response.status !== 200) {
            openNotificationModal("error", ["오류가 발생했습니다."], 2000);
        }
        notificationBtn.addEventListener("click", changeNotificationState);
    })
}
