const circleCenter = document.querySelector(".circle-center")
const leaderBtns = circleCenter.querySelector(".leader-btns");
const skipBtn = leaderBtns.querySelector(".skip-btn");
const exitBtn = leaderBtns.querySelector(".exit-btn");
const delegationBtn = leaderBtns.querySelector(".delegation-btn");
const groupMenu = document.querySelector(".group-menu");
var selectedMember = null;

skipBtn.addEventListener("click", clickSkipBtn);
exitBtn.addEventListener("click", clickExitBtn);
delegationBtn.addEventListener("click", clickDelegationBtn);

function selectGroupMember(event) {
    if (selectedMember !== null) {
        selectedMember.classList.remove("selected");
    }
    const member = event.target.closest(".group-member");
    selectedMember = member;
    member.classList.add("selected");
    openLeaderMenu(member);
    groupMenu.addEventListener("click", unselectGroupMember);
}

function openLeaderMenu(member) {
    if (member.classList.contains("order")) {
        leaderBtns.classList.add("order");
    } else {
        leaderBtns.classList.remove("order");
    }
    circleCenter.classList.replace("group-size", "leader-btns");
}

function unselectGroupMember(event) {
    if (event.target.closest(".group-member") ===  null && event.target.closest(".circle-center") ===  null) {
        selectedMember.classList.remove("selected");
        selectedMember = null;
        closeLeaderMenu();
        groupMenu.removeEventListener("click", unselectGroupMember);
    }
}

function closeLeaderMenu() {
    circleCenter.classList.replace("leader-btns", "group-size");
}

async function clickSkipBtn(event) {
    event.preventDefault();
    const selectedMemberNickname = selectedMember.querySelector(".profile-nickname").innerText
    const result = await openConfirmModal(`${selectedMemberNickname} 순서를 건너뛸까요?`);

    if (result) {
        const url = event.target.closest("a").href;

        fetch(url, {
            method: "PATCH"
        })
        .then(response => {
            if (response.status === 200) {
                openNotificationModal("success", ["순서를 건너뛰었어요."], 2000, () => location.reload());
            } else {
                openNotificationModal("error", ["이미 한 번 건너뛰었어요!", "내일 다시 건너뛸 수 있어요."], 2000);
            }
        });
    }
}

function clickExitBtn(event) {
    event.preventDefault();
    if (!isLeaderByGroupMember(selectedMember)) {
        const url = event.target.closest("a").href;
        exitByMember(url);
    } else {
        openNotificationModal("error", ["방장은 나갈 수 없습니다."], 2000);
    }
}

function isLeaderByGroupMember(groupMember) {
    return groupMember.querySelector(".crown") !== null;
}

async function exitByMember(url) {
    const selectedMemberNickname = selectedMember.querySelector(".profile-nickname").innerText;
    const result = await openConfirmModal(`${selectedMemberNickname}을 정말 내보낼까요?`, "내보내기 시 모든 데이터가 영구적으로 삭제됩니다.");

    if (result) {
        const body = { "nickname": selectedMemberNickname };

        fetch(url, {
            method: "PATCH",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        })
        .then(response => {
            if (response.status === 200) {
                openNotificationModal("success", ["내보내기를 완료했어요."], 2000, () => location.reload());
            } else {
                openNotificationModal("error", ["오류가 발생했습니다."], 2000);
            }
        });
    }
}

function clickDelegationBtn(event) {
    event.preventDefault();
    if (!isLeaderByGroupMember(selectedMember)) {
        const url = event.target.closest("a").href;
        delegationByMember(url);
    } else {
        openNotificationModal("error", ["이미 방장 입니다!"], 2000);
    }
}

async function delegationByMember(url) {
    const result = await openConfirmModal("방장을 넘길까요?");
    
    if (result) {
        const body = { "nickname": selectedMember.querySelector(".profile-nickname").innerText };

        fetch(url, {
            method: "PATCH",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        })
        .then(response => {
            if (response.status === 200) {
                openNotificationModal("success", ["방장을 넘겼습니다."], 2000, () => location.reload());
            } else {
                openNotificationModal("error", ["오류가 발생했습니다."], 2000);
            }
        });
    }
}
