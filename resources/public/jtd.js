document.addEventListener('click', function (e) {
    const vw = window.innerWidth;
    const vh = window.innerHeight;

    const x = e.clientX; // distance from left edge of viewport
    const y = e.clientY; // distance from top edge of viewport

    const xPercent = (x / vw) * 100;
    const yPercent = (y / vh) * 100;

    console.log(`x: ${xPercent.toFixed(2)}%, y: ${yPercent.toFixed(2)}%`);
});
