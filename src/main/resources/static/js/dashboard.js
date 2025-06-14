        document.addEventListener('DOMContentLoaded', function() {
            // Dados de exemplo - o ideal é carregar isso do controller
            const vendasData = {
                labels: ['D-6', 'D-5', 'D-4', 'D-3', 'D-2', 'Ontem', 'Hoje'],
                datasets: [{
                    label: 'Vendas (R$)',
                    data: [800, 1200, 950, 1500, 1300, 1800, 2050],
                    backgroundColor: 'rgba(74, 144, 226, 0.2)',
                    borderColor: 'rgba(74, 144, 226, 1)',
                    borderWidth: 1,
                    tension: 0.1
                }]
            };

            const produtosData = {
                labels: ['Produto A', 'Produto B', 'Produto C', 'Produto D', 'Produto E'],
                datasets: [{
                    label: 'Unidades Vendidas',
                    data: [300, 250, 180, 120, 90],
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)'
                    ],
                    borderWidth: 1
                }]
            };
            
            // Gráfico de Vendas
            const vendasCtx = document.getElementById('vendasChart').getContext('2d');
            new Chart(vendasCtx, {
                type: 'line',
                data: vendasData,
                options: { responsive: true, maintainAspectRatio: false }
            });

            // Gráfico de Produtos
            const produtosCtx = document.getElementById('produtosChart').getContext('2d');
            new Chart(produtosCtx, {
                type: 'bar',
                data: produtosData,
                options: { responsive: true, maintainAspectRatio: false, indexAxis: 'y' }
            });
        });