document.addEventListener("DOMContentLoaded", function () {
  const fotoInput = document.getElementById("foto-input");
  const avatarPreview = document.getElementById("avatar-preview");
  const fileNameDisplay = document.getElementById("file-name");
  const avatarWrapper = document.querySelector(".avatar-wrapper");

  if (fotoInput && avatarPreview && fileNameDisplay && avatarWrapper) {
    avatarWrapper.addEventListener("click", function () {
      fotoInput.click();
    });

    fotoInput.addEventListener("change", function () {
      const file = this.files[0];
      if (file) {
        fileNameDisplay.textContent = file.name;

        const reader = new FileReader();
        reader.onload = function (e) {
          avatarPreview.src = e.target.result;
        };
        reader.readAsDataURL(file);
      } else {
        fileNameDisplay.textContent = "Nenhum arquivo selecionado.";
      }
    });
  }
});
