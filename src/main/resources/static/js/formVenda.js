document.addEventListener("DOMContentLoaded", function () {
    const addBtn = document.getElementById("add-produto-btn");
    const produtoSelect = document.getElementById("produto-select");
    const qtdInput = document.getElementById("produto-qtd");
    const tbody = document.getElementById("itens-venda-tbody");
    const form = document.getElementById("vendaForm");

if (!addBtn || !produtoSelect || !qtdInput || !tbody || !form) {
    return;
}

addBtn.addEventListener("click", function () {
    const selectedOption = produtoSelect.options[produtoSelect.selectedIndex];
    if (!selectedOption.value) {
    alert("Por favor, selecione um produto.");
    return;
    }

    const produtoId = selectedOption.value;
    const produtoNome = selectedOption.text;
    const produtoPreco = parseFloat(selectedOption.getAttribute("data-preco"));
    const quantidade = parseInt(qtdInput.value);

    if (isNaN(quantidade) || quantidade <= 0) {
    alert("Por favor, insira uma quantidade válida.");
    return;
    }

    // Verifica se o item já foi adicionado
    if (
    tbody.querySelector(`input[name$="produto.id"][value="${produtoId}"]`)
    ) {
    alert("Este produto já foi adicionado à venda.");
    return;
    }

    const subtotal = produtoPreco * quantidade;
    const rowIndex = tbody.rows.length;
    const newRow = tbody.insertRow(); // Cria uma nova linha na tabela

    newRow.innerHTML = `
            <input type="hidden" name="itens[${rowIndex}].produto.id" value="${produtoId}">
            <input type="hidden" name="itens[${rowIndex}].quantidade" value="${quantidade}">
            <input type="hidden" name="itens[${rowIndex}].precoUnitario" value="${produtoPreco}">
            
            <td>${produtoNome}</td>
            <td>${quantidade}</td>
            <td>R$ ${produtoPreco.toFixed(2).replace(".", ",")}</td>
            <td>R$ ${subtotal.toFixed(2).replace(".", ",")}</td>
            <td><button type="button" class="action-icon delete" onclick="removerLinha(this)">❌</button></td>
        `;

    atualizarTotal();
});

form.addEventListener("submit", function (e) {
    const rows = tbody.getElementsByTagName("tr");
    for (let i = 0; i < rows.length; i++) {
    const inputs = rows[i].querySelectorAll('input[type="hidden"]');
    inputs.forEach((input) => {
        const oldName = input.getAttribute("name");
        const newName = oldName.replace(/\[\d+\]/, `[${i}]`);
        input.setAttribute("name", newName);
    });
    }
});

window.removerLinha = function (button) {
    button.closest("tr").remove();
    atualizarTotal();
};

function atualizarTotal() {
    let total = 0;
    const rows = tbody.getElementsByTagName("tr");
    for (let row of rows) {
    const subtotalText = row.cells[3].innerText
        .replace("R$ ", "")
        .replace(",", ".");
    total += parseFloat(subtotalText);
    }
    document.getElementById("valor-total-display").innerText = total
        .toFixed(2)
        .replace(".", ",");
    document.getElementById("valorTotal").value = total.toFixed(2);
}

  // Calcula o total inicial ao carregar a página (para o modo de edição)
atualizarTotal();
});
