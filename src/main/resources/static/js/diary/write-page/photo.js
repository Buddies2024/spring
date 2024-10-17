const photoBtn = document.querySelector(".photo-btn");
const photoInput = document.querySelector("#photo-input");

addUploadPhotoEvent();

function addUploadPhotoEvent() {
    photoBtn.addEventListener("click", clickPhotoInput);
    photoInput.addEventListener("change", uploadImage);
}

function clickPhotoInput(event) {
    event.preventDefault();
    photoInput.click();
}

function uploadImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onloadend = function () {
        photoBtn.classList.remove("photo-btn");
        photoBtn.innerHTML = `
                            <img src="${reader.result}" class="image">
                                <a class="cancel-btn cancel-icon">
                                    <img src="/images/diary/write-page/cancel-upload.svg" class="cancel-icon">
                                </a>
                            `;
        closeModal();
        addCancelPhotoEvent();
    };
    if (file) {
        reader.readAsDataURL(file);
    }
}

function addCancelPhotoEvent() {
    const cancelBtn = document.querySelector(".cancel-btn");

    cancelBtn.addEventListener("click", cancelUpload);
}

function cancelUpload() {
    photoBtn.classList.add("photo-btn");
    photoBtn.innerHTML = `
                         <img class="photo-icon">
                        `;
}
