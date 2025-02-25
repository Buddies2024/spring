import App from "./common/App.js";
import Router from "./common/Router.js";
import LoginPage from "./pages/common/LoginPage.js";
import GroupJoinPage from "./pages/group/join/GroupJoinPage.js";

const app = new App(document.querySelector(".main-screen"));
const router = new Router(app, "#");

// router.add("/", HomePage);
router.add("/login", LoginPage);
router.add("/group", GroupJoinPage);
