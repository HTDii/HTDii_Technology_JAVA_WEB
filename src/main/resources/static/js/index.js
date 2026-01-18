const canvas = document.getElementById("fireworksCanvas");
const ctx = canvas.getContext("2d");
canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

window.addEventListener("resize", () => {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
});

function random(min, max) {
    return Math.random() * (max - min) + min;
}

class Particle {
    constructor(x, y, color) {
        this.x = x;
        this.y = y;
        this.radius = 2;
        this.color = color;
        this.angle = Math.random() * 2 * Math.PI;
        this.speed = random(1, 5);
        this.alpha = 1;
        this.decay = random(0.01, 0.02);
    }

    update() {
        this.x += Math.cos(this.angle) * this.speed;
        this.y += Math.sin(this.angle) * this.speed;
        this.alpha -= this.decay;
    }

    draw() {
        ctx.globalAlpha = this.alpha;
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.radius, 0, 2 * Math.PI);
        ctx.fillStyle = this.color;
        ctx.fill();
        ctx.globalAlpha = 1;
    }
}

let particles = [];

function createFirework() {
    const x = random(100, canvas.width - 100);
    const y = random(100, canvas.height - 100);
    const color = `hsl(${Math.floor(random(0, 360))}, 100%, 60%)`;

    for (let i = 0; i < 30; i++) {
        particles.push(new Particle(x, y, color));
    }
}

// function animate() {
//     ctx.fillStyle = "rgba(15, 226, 43, 0.1)";
//     ctx.fillRect(0, 0, canvas.width, canvas.height);

//     particles = particles.filter(p => p.alpha > 0);
//     particles.forEach(p => {
//         p.update();
//         p.draw();
//     });

//     requestAnimationFrame(animate);
// }

setInterval(createFirework, 1500);
// animate();