document.addEventListener("DOMContentLoaded", function () {
    const phoneInput = document.getElementById("telefone");

    if (phoneInput) {
    phoneInput.addEventListener("input", function (e) {
        let value = e.target.value.replace(/\D/g, "");

      // Limita a quantidade de números a 11 (padrão para celular com DDD)
        value = value.substring(0, 11);

      // Aplica a formatação dinamicamente
        if (value.length > 10) {
        // Formato para celular: (XX) XXXXX-XXXX
        value = value.replace(/^(\d{2})(\d{5})(\d{4}).*/, "($1) $2-$3");
        } else if (value.length > 6) {
        // Formato para telefone fixo: (XX) XXXX-XXXX
        value = value.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, "($1) $2-$3");
        } else if (value.length > 2) {
        // Formato para DDD e início do número: (XX) XXXX
        value = value.replace(/^(\d{2})(\d*)/, "($1) $2");
        } else if (value.length > 0) {
        // Formato para apenas DDD: (XX
        value = value.replace(/^(\d*)/, "($1");
        }

      // Atualiza o valor do campo com a string formatada
        e.target.value = value;
    });
    }
});
