const STEP4_HTML = `
                        <div>
                            <img class="selected-character" style="width: 100px; height: 100px;">
                        </div>
                        <div style="width: 100%; height: 34px; position: relative; top: 16px;">
                            <span class="subject">캐릭터 이름을 지어주세요.</span>
                        </div>
                        <div class="input-box">
                            <div class="input-textarea">
                                <input class="nickname input-value" type="text" placeholder="이름" maxlength="6" spellcheck="false">
                            </div>
                        </div>
                        <div class="error-message" style="width: 283px; height: 34px; float: left; position: relative; left: 46px; top: 24px;">
                            <span class="error-text"></span>
                        </div>`;

export function drawStep4(direction) {
    const step_content = document.createElement("div");
    step_content.classList.add("step-content", direction, "step4");
    step_content.innerHTML = STEP4_HTML;
    note_body.appendChild(step_content);
    setTimeout(() => step_content.style.transform = "translateX(0)", 10);
    initStep4();
}

function initStep4() {
    nicknameBox = document.querySelector(".input-box");
    nickname = document.querySelector(".nickname");
    error = document.querySelector(".error-text");

    setProfileImage();
    nickname.addEventListener("input", async () => {
        error.innerText = await changeStyleAndGetErrorMessage();
    });
}

export async function confirmStep4() {
    if (error.innerText !== "") {
        return false;
    }
    if (nickname.value === "") {
        openNotificationModal("error", ["이름을 입력해주세요."], 2000);
        return false;
    }
    if (isCreateInStep4()) {
        steps[5].draw = drawStep5Create;
        return await createGroup();
    }
    steps[5].draw = drawStep5Join;
    return await joinGroup();
}

function isCreateInStep4() {
    return groupData.groupId === "";
}

function setProfileImage() {
    const profileImage = document.querySelector(".selected-character");

    profileImage.classList.add(groupData.profileImage);
}

async function changeStyleAndGetErrorMessage() {
    if (nickname.value !== "") {
        nicknameBox.classList.add("input");
        return await verifyNickname();
    }
    nicknameBox.classList.remove("input");
    return "";
}

async function verifyNickname() {
    const specialChar = /[\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"₩]/;
    const whiteSpace = /\s/;

    if (specialChar.test(nickname.value)) {
        return "특수문자는 사용할 수 없습니다.";
    }
    if (whiteSpace.test(nickname.value)) {
        return "공백을 포함할 수 없습니다.";
    }
    if (nickname.value.length > 5) {
        return "최대 5자까지 입력 가능합니다.";
    }
    return await isDuplicateNickname();
}

async function isDuplicateNickname() {
    if (isCreateInStep4()) {
        return "";
    }
    return await fetch(`/api/groups/${groupData.groupId}/nickname/verify?nickname=${nickname.value}`)
        .then(async response => {
            if (response.status !== 200) {
                throw await response.json();
            }
            return "";
        })
        .catch(data => data.message)
}

async function createGroup() {
    return await fetch("/api/groups",{
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            "groupName": groupData.groupName,
            "profileImage": groupData.profileImage,
            "nickname": nickname.value
        })
    })
        .then(response => {
            if (response.status !== 201) {
                throw response;
            }
            return response.json();
        })
        .then(data => {
            groupData.groupCode = data.code;
            groupData.groupId = data.groupId;
            return true;
        })
        .catch(async response => {
            if (response.status === 400) {
                throw await response.json();
            }
        })
        .catch(data => {
            const messages = data.message.split("\n");
            openNotificationModal("error", messages, 2000);
            return false;
        });
}

async function joinGroup() {
    return await fetch(`/api/groups/${groupData.groupId}/join`,{
        method: "PATCH",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            "profileImage": groupData.profileImage,
            "nickname": nickname.value
        })
    })
        .then(response => {
            if (response.status !== 200) {
                throw response;
            }
            return true;
        })
        .catch(async response => {
            if (response.status === 400  || response.status === 409) {
                throw await response.json();
            }
        })
        .catch(data => {
            const messages = data.message.split("\n");
            openNotificationModal("error", messages, 2000, prevStep);
            return false;
        });
}
