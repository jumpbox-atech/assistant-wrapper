window.onload = function () {
    var chatHistoryArea = document.getElementById("chat_history_area");
    chatHistoryArea.scrollTop = chatHistoryArea.scrollHeight;
};

document.addEventListener('DOMContentLoaded', function () {
    const textarea = document.getElementById('chat_question');
    const characterCount = document.getElementById('characterCount');

    textarea.addEventListener('input', function () {
        const remainingChars = 1000 - textarea.value.length;
        characterCount.textContent = remainingChars + ' of 1000 characters left';
    });
});


