document.addEventListener("DOMContentLoaded", function () {
  const currentPage = window.location.pathname.split("/").pop();
  const sidebarItems = document.querySelectorAll(".sidebar-nav a.sidebar-item");

  sidebarItems.forEach((item) => {
    const itemPage = item.getAttribute("href");
    if (itemPage === currentPage) {
      item.classList.add("active");
    } else {
      item.classList.remove("active");
    }
  });

  if (currentPage === "Relatorios.html") {
    const reportCards = document.querySelectorAll(".report-card");
    const reportContents = document.querySelectorAll(".report-content");
    const defaultReportContent = document.getElementById(
      "default-report-content",
    );
    const currentReportTitle = document.getElementById("relatorio-atual");

    reportContents.forEach((content) => {
      if (content.id !== "default-report-content") {
        content.style.display = "none";
      }
    });
    if (defaultReportContent) defaultReportContent.style.display = "block";

    reportCards.forEach((card) => {
      card.addEventListener("click", function (event) {
        event.preventDefault();

        const targetId = this.getAttribute("href").substring(1);
        const targetContent = document.getElementById(targetId);

        reportContents.forEach((content) => (content.style.display = "none"));

        if (targetContent) {
          targetContent.style.display = "block";
          const reportName = targetContent.querySelector("h4").innerText;
          if (currentReportTitle) currentReportTitle.innerText = reportName;
        } else {
          if (defaultReportContent)
            defaultReportContent.style.display = "block";
          if (currentReportTitle)
            currentReportTitle.innerText = "Resultados do Relatório";
        }
      });
    });
  }
});
