let t = 0, i = 0;
const $ = () => document.querySelector('img');
const show = () => $().classList.remove('hidden');

const effects = [
    t => $().style.opacity = t * t,
    t => $().style.filter = `blur(${100 * (1 - t)}px)`,
    t => $().style.clipPath = `circle(${t * Math.sqrt(t) * 75}% at center)`,
    t => $().style.clipPath = `inset(0 ${100 - t * 100}% 0 0)`,
    t => $().style.clipPath = `inset(0 0 0 ${100 - t * 100}%)`,
    t => $().style.clipPath = `inset(${100 - t * 100}% ${100 - t * 100}% 0 0)`,
    t => $().style.clipPath = `inset(0 0 ${100 - t * 100}% ${100 - t * 100}%)`,
    t => $().style.clipPath = `inset(0 0 ${100 - t * 100}% 0)`,
    t => $().style.clipPath = `inset(${100 - t * 100}% 0 0 0)`,
];

const applyEffect = s => {
    t = s;
    effects[i](t);
};

const inc = () => applyEffect(Math.min(t + 0.1, 1));
const dec = () => applyEffect(Math.max(t - 0.1, 0));

const newImg = () => {
    applyEffect(1);
    i = Math.floor(Math.random() * effects.length);
    applyEffect(0);
    show(); // back to normal
}

newImg();

document.addEventListener("keydown", e => {
    switch (e.key) {
        case "ArrowLeft":
            return dec();

        case "ArrowRight":
            return inc();
    }
});
