document.addEventListener('DOMContentLoaded', function () {
    let deleteButtons = document.querySelectorAll('.delete-button');
    let deleteLink = document.getElementById('deleteLink');

    deleteButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            const categoryId = this.getAttribute('data-category-id');
            const deletePath = this.getAttribute('data-delete-path');
            deleteLink.href = deletePath;
            const modal = new bootstrap.Modal(document.getElementById('deleteConfirmationModal'));
            modal.show();
        });
    });

    $(function () {
        $(".datetimepicker-input").datetimepicker({
            dateFormat: "yy-mm-dd",
            timeFormat: "HH:mm"
        });
    });

});