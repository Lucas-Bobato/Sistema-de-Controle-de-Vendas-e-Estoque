document.addEventListener('DOMContentLoaded', function() {
    
    // 1. Highlight the active sidebar item based on the current page
    const currentPage = window.location.pathname.split('/').pop();
    const sidebarItems = document.querySelectorAll('.sidebar-nav a.sidebar-item');

    sidebarItems.forEach(item => {
        const itemPage = item.getAttribute('href');
        if (itemPage === currentPage) {
            item.classList.add('active');
        } else {
            // Ensure other items are not active, in case the 'active' class was hardcoded in HTML
            item.classList.remove('active');
        }
    });

    // 2. Logic for the Reports page (Relatorios.html)
    if (currentPage === 'Relatorios.html') {
        const reportCards = document.querySelectorAll('.report-card');
        const reportContents = document.querySelectorAll('.report-content');
        const defaultReportContent = document.getElementById('default-report-content');
        const currentReportTitle = document.getElementById('relatorio-atual');

        // Hide all specific report details initially and show the default message
        reportContents.forEach(content => {
            if (content.id !== 'default-report-content') {
                content.style.display = 'none';
            }
        });
        if(defaultReportContent) defaultReportContent.style.display = 'block';

        reportCards.forEach(card => {
            card.addEventListener('click', function(event) {
                event.preventDefault(); // Prevent default anchor behavior

                // Get the target content ID from the card's href
                const targetId = this.getAttribute('href').substring(1);
                const targetContent = document.getElementById(targetId);
                
                // Hide all report contents
                reportContents.forEach(content => content.style.display = 'none');
                
                // Show the targeted report content
                if (targetContent) {
                    targetContent.style.display = 'block';
                    // Update the main title for the results section
                    const reportName = targetContent.querySelector('h4').innerText;
                    if(currentReportTitle) currentReportTitle.innerText = reportName;
                } else {
                    // If no specific content is found, show the default
                    if(defaultReportContent) defaultReportContent.style.display = 'block';
                    if(currentReportTitle) currentReportTitle.innerText = "Resultados do Relatório";
                }
            });
        });
    }

});