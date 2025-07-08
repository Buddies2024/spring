const confirm_modal = document.querySelector(".confirm-modal");
const confirm_modal_popup = confirm_modal.querySelector(".modal-popup")
const confirm_modal_text = confirm_modal_popup.querySelector(".modal-text");
const confirm_modal_warning_text = confirm_modal_popup.querySelector(".modal-warning-text");
const confirm_images = [
    "/images/common/confirm-modal/background.svg",
    "/images/common/confirm-modal/big-background.svg",
    "/images/common/confirm-modal/warning.svg",
];

preLoadImgage(confirm_images);

function openConfirmModal(message, warning_message=null) {
    confirm_modal_text.innerText = message;
    confirm_modal_popup.classList.remove("big");
    confirm_modal_warning_text.innerText = "";
    if (warning_message !== null) {
        confirm_modal_popup.classList.add("big");
        confirm_modal_warning_text.innerText = `(${warning_message})`;
    }
    confirm_modal.style.display = "block";

    return new Promise(resolve => {
        const confirm_btn = confirm_modal_popup.querySelector(".confirm-btn");
        confirm_btn.addEventListener("click", (event) => {
            closeConfirmModal();
            setTimeout(() => resolve(true));
        });

        const cancel_btn = confirm_modal_popup.querySelector(".cancel-btn");
        cancel_btn.addEventListener("click", (event) => {
            closeConfirmModal();
            setTimeout(() => resolve(false));
        })
    });
}

function closeConfirmModal() {
    confirm_modal.style.display = "none";
}
