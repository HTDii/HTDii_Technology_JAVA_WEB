const canvas = document.getElementById("canvas");
const ctx = canvas.getContext("2d");

/* ===== RESIZE ===== */
function resize() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
}
resize();
window.addEventListener("resize", resize);

/* ===== SNOW CONFIG ===== */
const snowflakes = Array.from({ length: 150 }, () => ({
    x: Math.random() * window.innerWidth,
    y: Math.random() * window.innerHeight,
    r: Math.random() * 2.5 + 0.5,
    d: Math.random() * 0.8 + 0.4
}));

/* ===== ANIMATION ===== */
function animate() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = "rgba(255,255,255,0.7)";

    snowflakes.forEach(s => {
        ctx.beginPath();
        ctx.arc(s.x, s.y, s.r, 0, Math.PI * 2);
        ctx.fill();

        s.y += s.d;

        if (s.y > canvas.height) {
            s.y = -10;
            s.x = Math.random() * canvas.width;
        }
    });

    requestAnimationFrame(animate);
}

animate();