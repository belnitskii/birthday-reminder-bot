document.addEventListener("DOMContentLoaded", function () {
    const dropdown = document.getElementById("dropdown");
    const selected = document.getElementById("selectedUser");
    const optionsList = document.getElementById("optionsList");
    const hiddenInput = document.getElementById("ownerId");

    if (!dropdown || !selected || !optionsList || !hiddenInput) return;

    selected.addEventListener("click", () => {
        optionsList.style.display = optionsList.style.display === "block" ? "none" : "block";
    });

    document.querySelectorAll("#optionsList li").forEach(option => {
        option.addEventListener("click", () => {
            const name = option.textContent;
            const id = option.getAttribute("data-id");

            selected.textContent = name;
            hiddenInput.value = id;
            optionsList.style.display = "none";
        });
    });

    document.addEventListener("click", function (event) {
        if (!dropdown.contains(event.target)) {
            optionsList.style.display = "none";
        }
    });
});
