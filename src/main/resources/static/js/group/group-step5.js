const STEP5_HTML_JOIN = `
                        <div style="width: 100%; height: 34px;">
                            <span class="subject" style="font-size: 20px;"><b>버디즈</b>에 참여되었어요!<br>우리만의 스프링을 시작해보아요.</span>
                        </div>
                        <div style="width: 375px; height: 70px; position: relative; top: 84px;">
                            <img src="/images/group/heart.svg" style="width: 144.2px; height: 70px;">
                        </div>`;

const STEP5_HTML_CREATE = `
                        <div style="width: 100%; height: 84px;">
                            <span class="subject" style="font-size: 20px; letter-spacing: 0.4px;">우리만의 스프링이 완성되었어요!<br>그룹코드를 공유해<br>친구들을 초대해보세요.</span>
                        </div>
                        <div style="width: 375px; height: 106px; position: relative; top: 56px;">
                            <img src="/images/group/heart.svg" style="width: 144.2px; height: 70px;">
                            <div style="width: 200px; height: 34px;">
                                <a href="#" class="copy-code">눌러서 코드 복사하기</a>
                            </div>
                        </div>`;

function drawStep5(html, direction) {
    const step_content = document.createElement("div");
    step_content.classList.add("step-content", direction);
    step_content.innerHTML = html;
    note_body.appendChild(step_content);
    setTimeout(() => step_content.style.transform = "translateX(0)", 10);
}

function drawStep5Create(direction) {
    drawStep5(STEP5_HTML_CREATE, direction);
    initStep5();
}

function drawStep5Join(direction) {
    drawStep5(STEP5_HTML_JOIN, direction);
}

function initStep5() {
    copyBtn = document.querySelector(".copy-code");

    copyBtn.addEventListener("click", () => navigator.clipboard.writeText(groupData.groupCode));
}

function confirmStep5() {
    window.localStorage.setItem("groupId", groupData.groupId);
    window.location.href =`/group/${groupData.groupId}`;
}
