const randInt = i => Math.floor(Math.random() * i);

const randJump = (len, current)=> {
    let next = randInt(len);
    while (next === current) {
        next = randInt(len);
    }

    location.href = `../${next}/`;
}

const addListener = (len, current) => document.addEventListener('keydown', (event) => {
    if (event.key === 'r' || event.key === 'R') {
        randJump(len, current);
    }
});
