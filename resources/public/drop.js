const $ = x => document.querySelector(x);

document.addEventListener('keydown', (event) => {
    if (event.key === 'z') {
        $('#drop').click();
    }
});

function fixSrc(target) {
    target.src = target.getAttribute('src2');
}
