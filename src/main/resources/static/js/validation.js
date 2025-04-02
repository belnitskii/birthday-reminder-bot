document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll('.validated-form').forEach(form => {
        form.addEventListener('submit', async function(event) {
            event.preventDefault();

            // Очистка ошибок
            form.querySelectorAll('.error-message').forEach(el => {
                el.textContent = '';
            });

            try {
                const formData = new FormData(form);
                const data = {
                    name: formData.get('name'),
                    birthdayDate: formData.get('birthdayDate')
                };

                const csrfToken = document.querySelector('input[name="_csrf"]').value;
                const response = await fetch('/api/person/validate', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    },
                    body: JSON.stringify(data)
                });

                if (!response.ok) {
                    const errors = await response.json();
                    console.log('Errors from server:', errors); // Логируем ошибки для отладки

                    // Обрабатываем ошибки
                    if (errors.nameError) {
                        document.getElementById('nameError').textContent = errors.nameError;
                    }
                    if (errors.dateError) {
                        document.getElementById('dateError').textContent = errors.dateError;
                    }
                } else {
                    // Если ошибок нет, отправляем оригинальную форму
                    form.submit();
                }
            } catch (error) {
                console.error('Ошибка валидации:', error);
            }
        });
    });
});