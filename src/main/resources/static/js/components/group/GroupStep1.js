import Component from "../Component.js";

export default class GroupStep1 extends Component {
    setup() {
        this.$state = {
            join: "",
            create: "",
            question: ""
        }
    }

    setEvent() {
        this.addEvent("click", ".join", () => {
            this.setState({ join: "selected", create: "", question: "다른 친구가 만든 스프링에 참여할까요?" });
        });
        this.addEvent("click", ".create", () => {
            this.setState({ join: "", create: "selected", question: "새로운 스프링을 만들까요?" });
        });
    }

    template() {
        return `
        <div class="step-content ${this.$props.direction} step1">
            <div style="width: 195px; height: 34px;">
                <span class="subject">스프링에 참여해요!</span>
            </div>
            <div class="group">
                <div class="join-btn">
                    <a href="javascript:void(0);" class="join circle ${this.$state.join}">
                        <div style="width: 64px; height: 90px;">
                            <img class="group-icon">
                            <span class="text group-text">그룹 가입</span>
                        </div>
                    </a>
                </div>
                <div class="create-btn">
                    <a href="javascript:void(0);" class="create circle ${this.$state.create}">
                        <div style="width: 64px; height: 90px;">
                            <img class="group-icon">
                            <span class="text group-text">그룹 생성</span>
                        </div>
                    </a>
                </div>
            </div>
            <div class="question">
                <span class="text question-text">${this.$state.question}</span>
            </div>
        </div>
        `;
    }

    mounted() {
        this.$props.direction = "stop";
        const stepCotnent = this.$target.querySelector(".step-content");
        setTimeout(() => stepCotnent.style.transform = "translateX(0)", 10);

        this.removeBackBtn();
    }

    removeBackBtn() {
        if (document.querySelector(".top-bar .back-btn")) {
            const backBtn = document.querySelector(".bar-back");
    
            backBtn.remove();
        }
    }

    confirm() {
        if (this.create === "selected") {
            this.$props = { type: "create" }
            return true;
        }
        if (this.join === "selected") {
            this.$props = { type: "join" }
            return true;
        }
        openNotificationModal("error", ["그룹 가입 또는 생성 중에 골라주세요!"], 2000);
        return false;
    }
}
