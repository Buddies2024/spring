const STEP3_HTML = `<div style="width: 100%; height: 34px;">
                                <span class="subject">나의 캐릭터를 골라요.</span>
                            </div>
                            <div class="line">
                                <div style="margin-left: 90px; height: 100%">
                                    <div class="character-btn">
                                        <a href="javascript:void(0);" class="character-icon">
                                            <img class="orange character-icon">
                                        </a>
                                    </div>
                                    <div class="character-btn">
                                        <a href="javascript:void(0);" class="character-icon">
                                            <img class="yellow character-icon">
                                        </a>
                                    </div>
                                </div>
                            </div>
                            <div class="line">
                                <div style="margin-left: 35px; height: 100%">
                                    <div class="character-btn">
                                        <a href="javascript:void(0);" class="character-icon">
                                            <img class="red character-icon">
                                        </a>
                                    </div>
                                    <div class="character-btn">
                                        <a href="javascript:void(0);" class="character-icon">
                                            <img class="purple character-icon">
                                        </a>
                                    </div>
                                    <div class="character-btn">
                                        <a href="javascript:void(0);" class="character-icon">
                                            <img class="green character-icon green-icon">
                                        </a>
                                    </div>
                                </div>
                            </div>
                            <div class="line">
                                <div style="margin-left: 90px; height: 100%">
                                    <div class="character-btn">
                                        <a href="javascript:void(0);" class="character-icon">
                                            <img class="blue character-icon blue-icon">
                                        </a>                                    
                                    </div>
                                    <div class="character-btn">
                                        <a href="javascript:void(0);" class="character-icon">
                                            <img class="navy character-icon">
                                        </a>
                                    </div>           
                                </div>
                            </div>`;

function drawStep3(direction) {
    const step_content = document.createElement("div");
    step_content.classList.add("step-content", direction, "step3");
    step_content.innerHTML = STEP3_HTML;
    note_body.appendChild(step_content);
    setTimeout(() => step_content.style.transform = "translateX(0)", 10);
    initStep3();
}

function initStep3() {
    const icons = note_body.querySelectorAll("a.character-icon");
    selectedIcon = null;

    Array.from(icons).forEach(icon => {
        icon.addEventListener("click", (event) => selectIcon(event));
    })

    if (groupData.groupId !== "") {
        viewSelectableCharacter();
    }
}

function selectIcon(event) {
    if (selectedIcon != null) {
        selectedIcon.classList.remove("selected");
    }
    selectedIcon = event.target.closest("div.character-btn");
    selectedIcon.classList.add("selected");
}

async function viewSelectableCharacter() {
    const groupId = groupData.groupId;

    const selectedImages = await fetch(`/api/groups/${groupId}/profile-image`)
        .then(response => response.json())
        .then(data => data.selectedImages);

    selectedImages.forEach(image => {
        const profileImage = document.querySelector(`.${image.profileImage}`);

        profileImage.parentElement.classList.add("gray");
    });

    if (selectedImages.length === 7) {
        openNotificationModal("error", ["그룹원이 꽉 차", "해당 그룹에 들어갈 수 없습니다."], 2000, prevStep);
    }
}

function confirmStep3() {
    if (selectedIcon != null) {
        groupData.profileImage = selectedIcon.children[0].children[0].classList[0];
        return true;
    }
    openNotificationModal("error", ["캐릭터를 선택해주세요."], 2000);
    return false;
}
