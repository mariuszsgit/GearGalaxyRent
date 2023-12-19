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

    // datepicker
    $(function () {
        $(".datetimepicker-input").datetimepicker({
            dateFormat: "yy-mm-dd",
            timeFormat: "HH:mm"
        });
    });

    // komunikat
    let alertBox = document.querySelector('.alert');
    if (alertBox) {
        setTimeout(function () {
            alertBox.style.display = 'none';
        }, 4000);
    }

    /*window.displayImage = function(input) {
        let imagePreview = document.getElementById('imagePreview');
        if (input.files && input.files[0]) {
            var reader = new FileReader();

            reader.onload = function(e) {
                imagePreview.src = e.target.result;
                imagePreview.style.display = 'block'; // Display
            }

            reader.readAsDataURL(input.files[0]);
        } else {
            imagePreview.style.display = 'none'; // Hide
        }
    }*/

    window.displayImage = function(input) {
        let imagePreview = document.getElementById('imagePreview');

        if (input.files && input.files[0]) {
            var reader = new FileReader();

            reader.onload = function(e) {
                // Efekt fading
                imagePreview.classList.add("fade-out");
                setTimeout(function() {
                    imagePreview.src = e.target.result;
                    imagePreview.classList.remove("fade-out");
                    imagePreview.style.display = 'block';
                }, 500); // Czas trwania fadingu
            }

            reader.readAsDataURL(input.files[0]);
        } else {
            imagePreview.style.display = 'none';
        }
    }

});