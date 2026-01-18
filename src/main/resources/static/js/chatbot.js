const chatBody = document.getElementById("chatBody");
const input = document.getElementById("messageInput");
const sendBtn = document.querySelector(".chat-input button");

let selectedLang = null;

/* =========================
   INPUT CONTROL
========================= */
function lockInput() {
    input.disabled = true;
    sendBtn.disabled = true;
}

function unlockInput() {
    input.disabled = false;
    sendBtn.disabled = false;
    input.focus();
}

/* =========================
   HIỂN THỊ MESSAGE
========================= */
function addMessage(text, type) {
    const div = document.createElement("div");
    div.className = "message " + type;
    div.innerText = text;
    chatBody.appendChild(div);
    chatBody.scrollTop = chatBody.scrollHeight;
}

/* =========================
   HIỂN THỊ OPTIONS
========================= */
function addOptions(options) {
    if (!options || options.length === 0) {
        // ✅ KHÔNG CÒN OPTION → MỞ CHAT
        unlockInput();
        return;
    }

    // ❌ CÒN OPTION → KHÓA CHAT
    lockInput();

    const box = document.createElement("div");
    box.className = "options-box";

    options.forEach(opt => {
        const btn = document.createElement("button");
        btn.className = "option-btn";
        btn.dataset.value = opt.value;
        btn.innerText = opt.label;
        box.appendChild(btn);
    });

    chatBody.appendChild(box);
    chatBody.scrollTop = chatBody.scrollHeight;
}

/* =========================
   EVENT DELEGATION
========================= */
chatBody.addEventListener("click", function (e) {
    const btn = e.target.closest(".option-btn");
    if (!btn) return;

    const value = btn.dataset.value;
    const box = btn.closest(".options-box");

    addMessage(btn.innerText, "user");
    if (box) box.remove();

    sendOption(value);
});

/* =========================
   CHỌN NGÔN NGỮ
========================= */
function selectLang(lang) {
    selectedLang = lang;
    addMessage(lang, "user");

    lockInput(); // ❌ CHƯA ĐƯỢC CHAT NGAY

    fetch("/api/chat/send", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message: lang })
    })
        .then(res => res.json())
        .then(data => {
            addMessage(data.reply || "❌ No connect", "bot");
            addOptions(data.options);

            const langBox = document.getElementById("langSelect");
            if (langBox) langBox.style.display = "none";
        })
        .catch(() => {
            addMessage("❌ No connect", "bot");
        });
}

/* =========================
   GỬI OPTION
========================= */
function sendOption(value) {
    lockInput();

    fetch("/api/chat/send", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            message: value,
            language: selectedLang
        })
    })
        .then(res => res.json())
        .then(data => {
            addMessage(data.reply || "❌ No connect", "bot");
            addOptions(data.options);
        })
        .catch(() => {
            addMessage("❌ No connect", "bot");
        });
}

/* =========================
   CHAT TỰ DO
========================= */
function sendMessage() {
    if (!selectedLang) {
        addMessage("⚠️ Vui lòng chọn ngôn ngữ trước", "bot");
        return;
    }

    const text = input.value.trim();
    if (!text) return;

    addMessage(text, "user");
    input.value = "";
    lockInput();

    fetch("/api/chat/send", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            message: text,
            language: selectedLang
        })
    })
        .then(res => res.json())
        .then(data => {
            addMessage(data.reply || "❌ No connect", "bot");
            addOptions(data.options);
        })
        .catch(() => {
            addMessage("❌ No connect", "bot");
        });
}

/* =========================
   RESET CHAT
========================= */
function resetChat() {
    chatBody.innerHTML = `
🤖 Please select your language to continue (chat will be temporarily locked until you complete the interaction).<br>
(言語を選択して続行してください。操作が完了するまでチャット機能は一時的にロックされます。<br>
Vui lòng chọn ngôn ngữ để bắt đầu, chức năng chat sẽ tạm khoá cho đến khi bạn chọn xong các bước tương tác.)
    `;
    selectedLang = null;
    lockInput();

    const langBox = document.getElementById("langSelect");
    if (langBox) langBox.style.display = "flex";
}

/* =========================
   SEND MESSAGE ON ENTER
========================= */
input.addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
        sendMessage();
    }
});