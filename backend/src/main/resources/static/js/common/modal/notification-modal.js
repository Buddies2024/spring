const notification_modal = document.querySelector(".notification-modal");
const notification_modal_content = notification_modal.querySelector(".modal-content");
const notification_modal_icon = notification_modal.querySelector(".modal-icon");
const notification_images = [
    "/images/common/notification-modal/background.svg",
    "/images/common/notification-modal/error.svg",
    "/images/common/notification-modal/success.svg",
];

var notification_timeout = null;
var afterMethod = null;

preLoadImgage(notification_images);

notification_modal.addEventListener("click", () => {
    clearTimeout(notification_timeout);
    closeNotificationModal();
});

function openNotificationModal(type, messages, time, method=null) {
    notification_modal_icon.children[0].classList = type;
    notification_modal_content.innerHTML = makeNotificationMessage(messages);
    notification_modal.style.display = "block";
    afterMethod = method;

    if (notification_timeout != null) {
        clearTimeout(notification_timeout);
    }

    notification_timeout = setTimeout(closeNotificationModal, time);
}

function makeNotificationMessage(messages) {
    var htmlMessage = ""

    messages.forEach(message => {
        htmlMessage += `<span class="modal-text">${message}</span>`
    })

    return htmlMessage;
}

function closeNotificationModal() {
    notification_modal.style.display = "none";
    notification_timeout = null;

    if (afterMethod != null) {
        afterMethod();
    }
}
