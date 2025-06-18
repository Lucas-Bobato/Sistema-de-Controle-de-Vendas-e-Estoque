function applyTheme(theme) {
  document.body.setAttribute("data-theme", theme);
  const logo = document.getElementById("sidebar-logo");
  if (logo) {
    const logoPath =
      theme === "dark"
        ? logo.getAttribute("data-logo-dark")
        : logo.getAttribute("data-logo-light");
    logo.setAttribute("src", logoPath);
  }
}

(function () {
  const savedTheme = localStorage.getItem("theme") || "light";
  applyTheme(savedTheme);
})();

document.addEventListener("DOMContentLoaded", () => {
  const themeCheckbox = document.getElementById("theme-checkbox");
  if (!themeCheckbox) return;

  const currentTheme = localStorage.getItem("theme") || "light";
  if (currentTheme === "dark") {
    themeCheckbox.checked = true;
  }

  themeCheckbox.addEventListener("change", () => {
    const newTheme = themeCheckbox.checked ? "dark" : "light";
    applyTheme(newTheme);
    localStorage.setItem("theme", newTheme);
  });
});

function getChartColorsForTheme() {
  const theme = document.body.getAttribute("data-theme");

  const darkThemeColors = {
    vendas: {
      line: "#58a6ff",
      fillStart: "rgba(88, 166, 255, 0.4)",
      fillEnd: "rgba(88, 166, 255, 0.0)",
    },
    produtos: ["#36a2eb", "#ff6384", "#4bc0c0", "#ff9f40", "#9966ff"],
  };

  const lightThemeColors = {
    vendas: {
      line: "#007bff",
      fillStart: "rgba(0, 123, 255, 0.3)",
      fillEnd: "rgba(0, 123, 255, 0.0)",
    },
    produtos: ["#28a745", "#ffc107", "#17a2b8", "#fd7e14", "#6f42c1"],
  };

  return theme === "dark" ? darkThemeColors : lightThemeColors;
}
