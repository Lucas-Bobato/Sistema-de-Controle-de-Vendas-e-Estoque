document.addEventListener('DOMContentLoaded', async function() {
    
    // Função para gerar cores aleatórias para os gráficos de barra
    const generateRandomColors = (numColors) => {
        const colors = [];
        for (let i = 0; i < numColors; i++) {
            const r = Math.floor(Math.random() * 255);
            const g = Math.floor(Math.random() * 255);
            const b = Math.floor(Math.random() * 255);
            colors.push(`rgba(${r}, ${g}, ${b}, 0.5)`);
        }
        return colors;
    };

    try {
        // Fetch data para o Gráfico de Vendas
        const vendasResponse = await fetch('/api/vendas/ultimos7dias');
        const vendasDataRaw = await vendasResponse.json();
        
        const vendasData = {
            labels: vendasDataRaw.labels,
            datasets: [{
                label: 'Vendas (R$)',
                data: vendasDataRaw.values,
                backgroundColor: 'rgba(74, 144, 226, 0.2)',
                borderColor: 'rgba(74, 144, 226, 1)',
                borderWidth: 2,
                tension: 0.4,
                fill: true
            }]
        };

        const vendasCtx = document.getElementById('vendasChart').getContext('2d');
        new Chart(vendasCtx, {
            type: 'line',
            data: vendasData,
            options: { responsive: true, maintainAspectRatio: false }
        });

    } catch (error) {
        console.error('Erro ao carregar dados de vendas:', error);
        document.getElementById('vendasChart').parentElement.innerHTML = '<p>Não foi possível carregar o gráfico de vendas.</p>';
    }

    try {
        // Fetch data para o Gráfico de Produtos
        const produtosResponse = await fetch('/api/produtos/mais-vendidos');
        const produtosDataRaw = await produtosResponse.json();

        const backgroundColors = generateRandomColors(produtosDataRaw.labels.length);
        
        const produtosData = {
            labels: produtosDataRaw.labels,
            datasets: [{
                label: 'Unidades Vendidas',
                data: produtosDataRaw.values,
                backgroundColor: backgroundColors,
                borderColor: backgroundColors.map(color => color.replace('0.5', '1')),
                borderWidth: 1
            }]
        };
        
        const produtosCtx = document.getElementById('produtosChart').getContext('2d');
        new Chart(produtosCtx, {
            type: 'bar',
            data: produtosData,
            options: { responsive: true, maintainAspectRatio: false, indexAxis: 'y' }
        });

    } catch (error) {
        console.error('Erro ao carregar dados de produtos:', error);
        document.getElementById('produtosChart').parentElement.innerHTML = '<p>Não foi possível carregar o gráfico de produtos.</p>';
    }
});