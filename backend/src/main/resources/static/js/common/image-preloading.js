function preLoadImgage(images) {
    images.forEach((image) => {
        const img = new Image();
        img.src = image;
    });
};
