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
  const notificationBell = document.getElementById("notification-bell");
  const notificationDropdown = document.getElementById("notification-dropdown");

  if (notificationBell && notificationDropdown) {
    fetchAndDisplayNotifications();

    notificationBell.addEventListener("click", (event) => {
      event.stopPropagation();
      notificationDropdown.style.display =
        notificationDropdown.style.display === "none" ? "block" : "none";
    });

    document.addEventListener("click", () => {
      notificationDropdown.style.display = "none";
    });

    notificationDropdown.addEventListener("click", (event) => {
      event.stopPropagation();
    });
  }
});

async function fetchAndDisplayNotifications() {
  try {
    const response = await fetch("/api/notificacoes");
    if (!response.ok) {
      console.error("Falha ao buscar notificações.");
      return;
    }
    const notificacoes = await response.json();
    const countElement = document.querySelector(".notification-count");
    const listElement = document.getElementById("notification-list");

    listElement.innerHTML = "";

    if (notificacoes.length > 0) {
      countElement.textContent = notificacoes.length;
      countElement.style.display = "block";

      notificacoes.forEach((n) => {
        const item = document.createElement("div");
        item.className = "notification-item";
        item.innerHTML = `<i class="bi bi-exclamation-triangle-fill icon"></i> <div>${n.mensagem}</div> <button class="close-notification" onclick="marcarComoLida(${n.id}, event)">&times;</button>`;
        listElement.appendChild(item);
      });
    } else {
      countElement.style.display = "none";
      listElement.innerHTML =
        '<div class="notification-item">Nenhuma nova notificação.</div>';
    }
  } catch (error) {
    console.error("Erro:", error);
  }
}

async function marcarComoLida(id, event) {
    event.stopPropagation();
    try {
        const response = await fetch(`/api/notificacoes/${id}/lida`, { method: 'POST' });
        if(response.ok) {
            fetchAndDisplayNotifications();
        } else {
            console.error('Falha ao marcar notificação como lida.');
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}

async function marcarTodasComoLidas(event) {
    event.preventDefault();
    try {
        const response = await fetch('/api/notificacoes/marcar-todas-lidas', { method: 'POST' });
        if(response.ok) {
            fetchAndDisplayNotifications();
        } else {
            console.error('Falha ao marcar todas as notificações como lidas.');
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}