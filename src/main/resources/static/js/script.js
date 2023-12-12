document.addEventListener('DOMContentLoaded', function () {
    let deleteButtons = document.querySelectorAll('.delete-button');
    let deleteCategoryLink = document.getElementById('deleteCategoryLink');

    deleteButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            const categoryId = this.getAttribute('data-category-id');
            deleteCategoryLink.href = '/category/delete/' + categoryId;
            const modal = new bootstrap.Modal(document.getElementById('deleteConfirmationModal'));
            modal.show();
        });
    });
});
