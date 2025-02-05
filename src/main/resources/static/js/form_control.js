function disableButton() {
    var button = document.getElementById("form_main_submit_button");
    var spinner = document.getElementById("form_main_submit_spinner");
    var icon = document.getElementById("form_main_submit_icon");
    var form = document.getElementById("form_main_content");

    if (form.checkValidity()) {
        button.disabled = true;
        icon.style.display = "none";
        spinner.classList.remove("d-none");
        form.submit();
    } else {
        form.reportValidity();
    }
}