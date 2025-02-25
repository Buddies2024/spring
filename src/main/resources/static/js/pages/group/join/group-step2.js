const STEP2_HTML_CREATE = `
                        <div style="width: 100%; height: 34px;">
                            <span class="subject">스프링의 이름을 지어주세요.</span>
                        </div>
                        <div class="input-box">
                            <div class="input-textarea">
                                <input class="group-name input-value" type="text" placeholder="그룹명" spellcheck="false" maxlength="11">
                            </div>
                        </div>
                        <div class="error-message" style="width: 283px; height: 34px; float: left; position: relative; left: 46px; top: 24px;">
                            <span class="error-text"></span>
                        </div>`

const STEP2_HTML_JOIN = `
                        <div style="width: 100%; height: 34px;">
                            <span class="subject">그룹코드를 입력해주세요.</span>
                        </div>
                        <div class="input-box">
                            <div class="input-textarea">
                                <input class="group-code input-value" type="text" placeholder="그룹코드" spellcheck="false">
                            </div>
                        </div>`

export function drawStep2(html, direction, stepType) {
    const step_content = document.createElement("div");
    step_content.classList.add("step-content", direction, stepType);
    step_content.innerHTML = html;
    note_body.appendChild(step_content);
    setTimeout(() => step_content.style.transform = "translateX(0)", 10);
    addBackBtn();
    initStep2();
}

function drawCreateGroup(direction) {
    drawStep2(STEP2_HTML_CREATE, direction, "step2-create");
}

function drawJoinGroup(direction) {
    drawStep2(STEP2_HTML_JOIN, direction, "step2-join");
}

function initStep2() {
    inputValue = document.querySelector(".input-value");
    error = document.querySelector(".error-text");

    inputValue.addEventListener("input", () => {
        changeBoxBorderStyle();
        if (isCreateInStep2()) {
            error.innerText = verifyGroupName();
        }
    })
}

export async function confirmStep2() {
    if (isJoinInStep2()) {
        return await matchGroupByGroupCode()
    }
    if (inputValue.value === "") {
        openNotificationModal("error", ["그룹명을 입력해주세요."], 2000);
        return false;
    }
    if (isCreateInStep2() && error.innerText === "") {
        groupData.groupName = inputValue.value;
        return true;
    }
    return false;
}

function isCreateInStep2() {
    return inputValue.classList.contains("group-name");
}

function isJoinInStep2() {
    return inputValue.classList.contains("group-code");
}

function changeBoxBorderStyle() {
    const inputBox = document.querySelector(".input-box");

    if (inputValue.value !== "" && !inputBox.classList.contains("input")) {
        inputBox.classList.add("input");
    }
    if (inputValue.value === "" && inputBox.classList.contains("input")) {
        inputBox.classList.remove("input");
    }
}

function verifyGroupName() {
    const groupName = document.querySelector(".group-name");
    const specialChar = /[\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]/;
    const whiteSpace = /\s/;

    if (specialChar.test(groupName.value)) {
        return "특수문자는 사용할 수 없습니다.";
    }
    if (whiteSpace.test(groupName.value)) {
        return "공백을 포함할 수 없습니다.";
    }
    if (groupName.value.length > 10) {
        return "최대 10자까지 입력 가능합니다.";
    }
    return "";
}

async function matchGroupByGroupCode() {
    const groupCode = document.querySelector(".group-code");

    return fetch("/api/groups/code/verify", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            "code": groupCode.value
        })
    })
        .then(async response => {
            if (response.status !== 200) {
                throw await response.json();
            }
            return response.json();
    })
        .then(data => {
            groupData.groupId = data.groupId;
            return true;
        })
        .catch(data => {
            openNotificationModal("error", [data.message], 2000);
            return false;
        });
}
