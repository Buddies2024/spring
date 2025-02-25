import ImageUtil from "../../common/ImageUtil.js"
import Component from "../../components/Component.js";

export default class StartPage extends Component {
    setup() {
        window.location.hash = "";
        const logo_images = [
            "/images/start-page/logo.png"
        ];
        ImageUtil.preLoadImage(logo_images);

        this.$state = {
            animationState: "",
            logoUrl: `/images/start-page/line.gif?${Math.random()}`
        }
    }

    setEvent() {
        this.addEvent("click", ".background", () => {
            if (this.$state.animationState === "end") {
                window.location.hash = this.getHash();
            } else {
                this.endAnimation();
            }
        })
    }

    template() {
        return `
        <div class="background start-page">
            <img class="logo ${this.$state.animationState}" src="${this.$state.logoUrl}"/>
            <div class="title-box">
                <span class="text spring" >스프링</span>
            </div>
        </div>
        `;
    }
    
    mounted() {
        setTimeout(() => { this.endAnimation() }, 2400);
    }

    endAnimation() {
        if (this.$state.animationState != "end") {
            this.setState({ animationState: "end" });
        }
    }

    getHash() {
        const userData = {
            shouldLogin: false,
            groupId: ""
        };
        if (userData.shouldLogin) {
            return "/login";
        }
        if (userData.groupId === "") {
            return `/group`;
        }
        this.$props = { groupId: userData.groupId };
        return "/";
    }
}
