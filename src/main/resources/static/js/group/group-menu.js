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
        openNotificationModal("success", ["코드 복사에 성공했습니다."], 500);
    } catch {
        openNotificationModal("error", ["오류가 발생했습니다."], 2000);
    }
});
notificationBtn.addEventListener("click", changeNotificationState);

function openMenu() {
    fetch(`/api/groups/${groupId}/members`)
        .then(response => response.json())
        .then(data => drawMenu(data));
    groupMenu.style.display = "block";
    groupMenu.classList.add("blur");
    setTimeout(() => menu.style.transform = "translateX(0)", 10);
}

function closeMenu(event) {
    if (event.target === groupMenu) {
        menu.style.transform = "translateX(100%)"
        groupMenu.classList.remove("blur");
        setTimeout(() => groupMenu.style.display = "none", 300);
        removeMembers();
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
        groupLeaveBtn.innerHTML = '<img src="/images/group/menu/delete.svg" />';
        groupLeaveBtn.removeEventListener("click", leaveGroup);
        groupLeaveBtn.addEventListener("click", deleteGroup);
    } else {
        groupLeaveBtn.innerText = '<img src="/images/group/menu/leave.svg" />';
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
    return `<a class="profile-image" href="#">
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

function changeNotificationState(event) {
    event.preventDefault();
    const notificationBtn = event.target.closest("a.notification");

    if (notificationBtn.classList.contains("on")) {
        notificationBtn.classList.replace("on", "off");
        notificationBtn.innerHTML = "<img />알림 OFF";
    } else {
        notificationBtn.classList.replace("off", "on");
        notificationBtn.innerHTML = "<img />알림 ON";
    }
}
