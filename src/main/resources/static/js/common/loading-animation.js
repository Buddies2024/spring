const loadingAnimation = document.querySelector('.loading-animation');
const lottieAnimation = bodymovin.loadAnimation({
    container: loadingAnimation.querySelector('.animation'),
    path: '/images/common/loading-animation.json',
    renderer: 'svg',
    loop: true,
    autoplay: false
});

function playLoadingAnimation() {
    loadingAnimation.style.display = "block";
    lottieAnimation.stop();
    lottieAnimation.play();
}

function stopLoadingAnimation() {
    loadingAnimation.style.display = "none";
    lottieAnimation.stop();
}

