function redirect(url) {
    window.location.href = url;
}

function replace(url) {
    window.location.replace(url);
}

document.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "visible" && !/^\/groups\/[A-Za-z0-9]{8}\/diaries$/.test(location.pathname)) {
        reloadPage();
    }
});

function reloadPage() {
    location.reload();
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
