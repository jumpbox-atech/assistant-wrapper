addEventListener("DOMContentLoaded", (event) => {
    const password = document.getElementById("wl-new_password");
    const passwordAlert = document.getElementById("password-alert");
    const lengIcon = document.getElementById("leng-icon");
    const bigLetterIcon = document.getElementById("big-letter-icon");
    const numIcon = document.getElementById("num-icon");
    const specialCharIcon = document.getElementById("special-char-icon");

    password.addEventListener("focus", () => {
        passwordAlert.classList.remove("d-none");
        if (!password.classList.contains("is-valid")) {
            password.classList.add("is-invalid");
        }
    });

    password.addEventListener("input", () => {
        const value = password.value;
        const isLengthValid = value.length >= 8;
        const hasUpperCase = /[A-Z]/.test(value);
        const hasNumber = /\d/.test(value);
        const hasSpecialChar = /[!@#%&*_?|()\[\]{}]/.test(value);

        lengIcon.innerHTML = isLengthValid ? '<i class="fas fa-check text-success me-2"></i>' : '<i class="fas fa-times text-danger me-3"></i>';
        bigLetterIcon.innerHTML = hasUpperCase ? '<i class="fas fa-check text-success me-2"></i>' : '<i class="fas fa-times text-danger me-3"></i>';
        numIcon.innerHTML = hasNumber ? '<i class="fas fa-check text-success me-2"></i>' : '<i class="fas fa-times text-danger me-3"></i>';
        specialCharIcon.innerHTML = hasSpecialChar ? '<i class="fas fa-check text-success me-2"></i>' : '<i class="fas fa-times text-danger me-3"></i>';

        const isPasswordValid = isLengthValid && hasUpperCase && hasNumber && hasSpecialChar;

        if (isPasswordValid) {
            password.classList.remove("is-invalid");
            password.classList.add("is-valid");

            passwordAlert.classList.remove("alert-warning");
            passwordAlert.classList.add("alert-success");
        } else {
            password.classList.remove("is-valid");
            password.classList.add("is-invalid");

            passwordAlert.classList.add("alert-warning");
            passwordAlert.classList.remove("alert-success");
        }

        const confirmPass = document.getElementById("wl-confirm_password");
        const confirmPassword = confirmPass.value;
        const confirmPassIcon = document.getElementById("confirm_password-icon");
        const submitButton = document.getElementById("form_main_submit_button");

        if (value === confirmPassword && isPasswordValid) {
            confirmPassIcon.innerHTML = '<i class="fas fa-check text-success me-2"></i>';
            confirmPass.classList.remove("is-invalid");
            confirmPass.classList.add("is-valid");
            submitButton.disabled = false;
        } else {
            confirmPassIcon.innerHTML = '<i class="fas fa-times text-danger me-3"></i>';
            confirmPass.classList.remove("is-valid");
            confirmPass.classList.add("is-invalid");
            submitButton.disabled = true;
        }
    });

    const confirmPass = document.getElementById("wl-confirm_password");

    confirmPass.addEventListener("input", () => {
        const confirmPassword = confirmPass.value;
        const passwordValue = password.value;
        const confirmPassIcon = document.getElementById("confirm_password-icon");
        const submitButton = document.getElementById("form_main_submit_button");

        if (passwordValue === confirmPassword && confirmPassword.length > 0) {
            confirmPassIcon.innerHTML = '<i class="fas fa-check text-success me-2"></i>';
            confirmPass.classList.remove("is-invalid");
            confirmPass.classList.add("is-valid");
            submitButton.disabled = false;
        } else {
            confirmPassIcon.innerHTML = '<i class="fas fa-times text-danger me-3"></i>';
            confirmPass.classList.remove("is-valid");
            confirmPass.classList.add("is-invalid");
            submitButton.disabled = true;
        }
    });

    password.addEventListener("blur", () => {
        passwordAlert.classList.add("d-none");
    });
});