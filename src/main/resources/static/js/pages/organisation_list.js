$(document).ready(function () {
    $('.table').DataTable({
        "columnDefs": [{"targets": 0, "orderable": false}, {"targets": 1}, {"targets": 2, "orderable": false,}]
    });
});
var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
});

function showConfirmationModal(button) {
    var fieldValue = button.getAttribute('field-data');
    var deleteUrl = '/admin/organisation/delete/' + fieldValue;
    document.getElementById('deleteLink').href = deleteUrl;
    $('#confirmationModal').modal('show');
}
