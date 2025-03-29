document.addEventListener("DOMContentLoaded", function() {
    // Обработчик для всех форм с классом validated-form
    document.querySelectorAll('.validated-form').forEach(form => {
        form.addEventListener('submit', async function(event) {
            event.preventDefault();

            // Очищаем предыдущие ошибки
            form.querySelectorAll('.error-message').forEach(el => {
                el.textContent = '';
            });

            try {
                const formData = Object.fromEntries(new FormData(form));
                const response = await fetch('/api/person/validate', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                });

                if (!response.ok) {
                    const errors = await response.json();
                    // Показываем ошибки
                    Object.entries(errors).forEach(([field, message]) => {
                        const errorElement = document.getElementById(`${field}`);
                        if (errorElement) {
                            errorElement.textContent = message;
                        }
                    });
                } else {
                    // Если ошибок нет, отправляем форму
                    form.submit();
                }
            } catch (error) {
                console.error('Ошибка:', error);
            }
        });
    });
});