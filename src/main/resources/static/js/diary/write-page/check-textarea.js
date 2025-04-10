const textareas = Array.from(document.querySelectorAll(".diary-content"));

initCheckTextarea();

function initCheckTextarea() {
    const mobileOs = getMobileOS();

    if (mobileOs === "iOS") {
        addEventTextareasByIos();
    }
}

function getMobileOS() {
    const userAgent = navigator.userAgent || navigator.vendor || window.opera;
  
    if (/android/i.test(userAgent)) {
      return "Android";
    }
  
    // iOS는 iPad, iPhone, iPod 포함 (Mac과 구분 필요)
    if (/iPad|iPhone|iPod/.test(userAgent) && !window.MSStream) {
      return "iOS";
    }
  
    return "unknown";
}
