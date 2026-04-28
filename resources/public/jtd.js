const $ = x => document.querySelector(x);

const click = (j, e) => {
    const rect = e.currentTarget.getBoundingClientRect();

    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const xPercent = (x / rect.width) * 100;
    const yPercent = (y / rect.height) * 100;

    $('#i').value = j;
    $('#x').value = xPercent;
    $('#y').value = yPercent;
    $('#push').click();

};

const rightclick = (j, e) => {
    e.preventDefault();
    if (confirm('Skip image?')) {
        $('#skip-i').value = j;
        $('#skip').click();
    }
};

document.addEventListener('keydown', (event) => {
    if (event.key === 'z') {
        $('#pop').click();
    }
    if (event.key === 'p') {
        $('#pdf').click();
    }
});
