document.addEventListener("DOMContentLoaded", async function () {
const chartColors = getChartColorsForTheme();

try {
const vendasCtx = document.getElementById("vendasChart").getContext("2d");
const vendasResponse = await fetch("/api/vendas/ultimos7dias");
const vendasDataRaw = await vendasResponse.json();

const gradient = vendasCtx.createLinearGradient(
    0,
    0,
    0,
    vendasCtx.canvas.clientHeight
);
gradient.addColorStop(0, chartColors.vendas.fillStart);
gradient.addColorStop(1, chartColors.vendas.fillEnd);

const vendasData = {
    labels: vendasDataRaw.labels,
    datasets: [
    {
        label: "Vendas (R$)",
        data: vendasDataRaw.values,
        fill: true,
        backgroundColor: gradient,
        borderColor: chartColors.vendas.line,
        pointBackgroundColor: chartColors.vendas.line,
        pointBorderColor: "#fff",
        pointHoverRadius: 6,
        borderWidth: 2.5,
        tension: 0.4,
    },
    ],
};

new Chart(vendasCtx, {
    type: "line",
    data: vendasData,
    options: {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
        y: { grid: { color: "rgba(128, 128, 128, 0.2)" } },
        x: { grid: { display: false } },
    },
    plugins: { legend: { display: false } },
    },
});
} catch (error) {
console.error("Erro ao carregar dados de vendas:", error);
document.getElementById("vendasChart").parentElement.innerHTML =
    "<p>Não foi possível carregar o gráfico de vendas.</p>";
}

try {
const produtosResponse = await fetch("/api/produtos/mais-vendidos");
const produtosDataRaw = await produtosResponse.json();

const produtosData = {
    labels: produtosDataRaw.labels,
    datasets: [
    {
        label: "Unidades Vendidas",
        data: produtosDataRaw.values,
        backgroundColor: chartColors.produtos,
        borderRadius: 4,
        borderWidth: 0,
    },
    ],
};

const produtosCtx = document
    .getElementById("produtosChart")
    .getContext("2d");
new Chart(produtosCtx, {
    type: "bar",
    data: produtosData,
    options: {
    indexAxis: "y",
    responsive: true,
    maintainAspectRatio: false,
    scales: {
        x: { grid: { color: "rgba(128, 128, 128, 0.2)" } },
        y: { grid: { display: false } },
    },
    plugins: { legend: { display: false } },
    },
});
} catch (error) {
console.error("Erro ao carregar dados de produtos:", error);
document.getElementById("produtosChart").parentElement.innerHTML =
    "<p>Não foi possível carregar o gráfico de produtos.</p>";
}
});
