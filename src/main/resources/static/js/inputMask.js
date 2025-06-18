document.addEventListener("DOMContentLoaded", function () {
    const phoneInput = document.getElementById("telefone");

    if (phoneInput) {
    phoneInput.addEventListener("input", function (e) {
        let value = e.target.value.replace(/\D/g, "");

      // Limita a quantidade de números a 11 (padrão para celular com DDD)
        value = value.substring(0, 11);

      // Aplica a formatação dinamicamente
        if (value.length > 10) {
        value = value.replace(/^(\d{2})(\d{5})(\d{4}).*/, "($1) $2-$3");
        } else if (value.length > 6) {
        value = value.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, "($1) $2-$3");
        } else if (value.length > 2) {
        value = value.replace(/^(\d{2})(\d*)/, "($1) $2");
        } else if (value.length > 0) {
        value = value.replace(/^(\d*)/, "($1");
        }

        e.target.value = value;
    });
    }
});
