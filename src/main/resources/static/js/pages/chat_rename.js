document.addEventListener('DOMContentLoaded', function () {
    const textarea = document.getElementById('description');
    const characterCount = document.getElementById('characterCount');

    textarea.addEventListener('input', function () {
        const remainingChars = 100 - textarea.value.length;
        characterCount.textContent = remainingChars + ' of 100 characters left';
    });
});
