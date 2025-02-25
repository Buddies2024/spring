const STEP5_HTML_JOIN = `
                        <div style="width: 100%; height: 34px;">
                            <span class="subject" style="font-size: 20px;"><b>버디즈</b>에 참여되었어요!<br>우리만의 스프링을 시작해보아요.</span>
                        </div>
                        <div style="width: 375px; height: 70px; position: relative; top: 84px;">
                            <img src="/images/group/create-join-page/heart.svg" style="width: 144.2px; height: 70px;">
                        </div>`;

const STEP5_HTML_CREATE = `
                        <div style="width: 100%; height: 84px;">
                            <span class="subject" style="font-size: 20px; letter-spacing: 0.4px;">우리만의 스프링이 완성되었어요!<br>그룹코드를 공유해<br>친구들을 초대해보세요.</span>
                        </div>
                        <div style="width: 375px; height: 106px; position: relative; top: 56px;">
                            <img src="/images/group/create-join-page/heart.svg" style="width: 144.2px; height: 70px;">
                            <div style="width: 200px; height: 34px;">
                                <a href="javascript:void(0);" class="copy-code">눌러서 코드 복사하기</a>
                            </div>
                        </div>`;

export function drawStep5(html, direction, stepType) {
    const step_content = document.createElement("div");
    step_content.classList.add("step-content", direction, stepType);
    step_content.innerHTML = html;
    note_body.appendChild(step_content);
    setTimeout(() => step_content.style.transform = "translateX(0)", 10);
    removeBackBtn();
}

function drawStep5Create(direction) {
    drawStep5(STEP5_HTML_CREATE, direction, "step5-create");
    initStep5();
}

function drawStep5Join(direction) {
    drawStep5(STEP5_HTML_JOIN, direction, "step5-join");
}

function initStep5() {
    copyBtn = document.querySelector(".copy-code");

    copyBtn.addEventListener("click", () => {
        try {
            navigator.clipboard.writeText(groupData.groupId);
            openNotificationModal("success", ["코드 복사에 성공했습니다."], 500);
        } catch {
            openNotificationModal("error", ["오류가 발생했습니다."], 2000);
        }
    });
}

export function confirmStep5() {
    window.location.href = `/groups/${groupData.groupId}`;
}
