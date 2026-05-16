const $ = x => document.querySelector(x);

document.addEventListener('keydown', (event) => {
    if (event.key === 'n') {
        $('#new-slideshow').click();
    }
});
