import Component from "../../../components/Component.js";
import GroupStep1 from "../../../components/group/GroupStep1.js";

export default class GroupJoinPage extends Component {
    setEvent() {
        this.addEvent("click", ".confirm-btn", () => this.confirmStep);
    }
    
    template() {
        return `
        <div class="background group-join-page">
            <div class="top-bar">
                <div class="logo">
                    <img class="logo-icon" src="/images/group/create-join-page/logo.svg">
                </div>
            </div>
            <div class="content">
                <div class="left-margin"></div>
                <div class="right-margin"></div>
                <div class="note">
                    <div class="note-header">
                        <div class="step-bar">
                            <div class="step-icon step1 fill"></div>
                            <div class="step-icon step2"></div>
                            <div class="step-icon step3"></div>
                            <div class="step-icon step4"></div>
                            <div class="step-icon step5"></div>
                        </div>
                    </div>
                    <div class="note-body">
                    </div>
                    <div class="note-footer">
                        <a href="javascript:void(0);" class="confirm-btn">
                            <span class="confirm-text">확인</span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
        `;
    }

    mounted() {
        const steps = {
            1: GroupStep1,
            // 2: GroupStep2,
            // 3: GroupStep3,
            // 4: GroupStep4,
            // 5: GroupStep5
        }
        this.steps = steps;
        this.note_body = document.querySelector(".note-body");
        this.stepIcons = Array.from(document.querySelector(".step-bar").children);

        const groupData = {
            "groupId": "",
            "groupName": "",
            "profileImage": "",
            "nickname": "",
            "groupCode": ""
        }
        this.currentStep = 1;

        setTimeout(() => this.step = new this.steps[1](this.note_body, { direction: "stop" }), 350);
    }

    confirmStep() {
        if (this.step.confirm()) {
            this.nextStep();
        }
    }

    nextStep() {
        this.addStepIcon();
        this.removeStep(-1);
        this.drawStep(this.currentStep, "next");
    }

    addStepIcon() {
        this.currentStep += 1;
        const stepIcon = this.stepIcons.find((stepIcon) => !stepIcon.classList.contains("fill"));
        stepIcon.classList.add("fill");
    }
    
    removeStep(direction) {
        const step_content = this.note_body.children[0];
        step_content.style.transform = `translateX(${100 * direction}%)`;
        setTimeout(() => step_content.remove(), 300);
    }
    
    drawStep(stepNumber, direction) {
        const props = this.step.$props;
        props.direction = direction;
        setTimeout(() => this.step = new this.steps[stepNumber](this.note_body, props), 350);
    }

    prevStep() {
        deleteStepIcon();
        removeStep(1);
        drawStep(this.currentStep, "prev");
    }
    
    deleteStepIcon() {
        this.currentStep -= 1;
        const stepIcon = this.stepIcons.findLast((stepIcon) => stepIcon.classList.contains("fill"));
        stepIcon.classList.remove("fill");
    }
}
