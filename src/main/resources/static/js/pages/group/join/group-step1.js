const STEP1_HTML = `
                    <div style="width: 195px; height: 34px;">
                        <span class="subject">스프링에 참여해요!</span>
                    </div>
                    <div class="group">
                        <div class="join-btn">
                            <a href="javascript:void(0);" class="join circle">
                                <div style="width: 64px; height: 90px;">
                                    <img class="group-icon">
                                    <span class="text group-text">그룹 가입</span>
                                </div>
                            </a>
                        </div>
                        <div class="create-btn">
                            <a href="javascript:void(0);" class="create circle">
                                <div style="width: 64px; height: 90px;">
                                    <img class="group-icon">
                                    <span class="text group-text">그룹 생성</span>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="question">
                        <span class="text question-text"></span>
                    </div>`

export function drawStep1(direction) {
    const step_content = document.createElement("div");
    step_content.classList.add("step-content", direction, "step1");
    step_content.innerHTML = STEP1_HTML;
    note_body.appendChild(step_content);
    setTimeout(() => step_content.style.transform = "translateX(0)", 10);
    removeBackBtn();
    initStep1();
}

function initStep1() {
    joinBtn = document.querySelector(".join");
    createBtn = document.querySelector(".create");
    question = document.querySelector(".question-text")
    
    joinBtn.addEventListener("click", clickGroupJoinBtn);
    createBtn.addEventListener("click", clickGroupCreateBtn);
}

function clickGroupJoinBtn(event) {
    changeStyle(joinBtn, createBtn);
    question.innerText = "다른 친구가 만든 스프링에 참여할까요?";
}

function clickGroupCreateBtn(event) {
    changeStyle(createBtn, joinBtn);
    question.innerText = "새로운 스프링을 만들까요?";
}

function changeStyle(selected, unselected) {
    selected.classList.add("selected");
    unselected.classList.remove("selected");
}

export function confirmStep1() {
    if (createBtn.classList.contains("selected")) {
        steps[2].draw = drawCreateGroup
        return true;
    }
    if (joinBtn.classList.contains("selected")) {
        steps[2].draw = drawJoinGroup
        return true;
    }
    openNotificationModal("error", ["그룹 가입 또는 생성 중에 골라주세요!"], 2000);
    return false;
}
