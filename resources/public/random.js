document.addEventListener('keydown', (event) => {
    if (event.key === 'r' || event.key === 'R') {
        document.querySelector('#randomLink').click();
    }
    if (event.key === 'e' || event.key === 'E') {
        document.querySelector('#editLink').click();
    }
});

function fixSrc(target) {
    target.src = target.getAttribute('src2');
}
