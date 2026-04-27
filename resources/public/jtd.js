const $ = x => document.querySelector(x);

const listen = () => {
    $('#screen').addEventListener('click', function (e) {
        e.preventDefault();
        const vw = window.innerWidth;
        const vh = window.innerHeight;

        const x = e.clientX; // distance from left edge of viewport
        const y = e.clientY; // distance from top edge of viewport

        const xPercent = (x / vw) * 100;
        const yPercent = (y / vh) * 100;
        console.log('aa', xPercent, yPercent);

        $('#x').value = xPercent;
        $('#y').value = yPercent;
        $('#push').click();
    });

    document.addEventListener('keydown', (event) => {
        if (event.key === 'z') {
            $('#pop').click();
        }
    });
}
