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
document.body.setAttribute("data-theme", savedTheme);
})();

document.addEventListener("DOMContentLoaded", () => {
if (window.location.pathname.includes("/configuracoes")) {
const themeSelector = document.getElementById("theme-selector");
const savedTheme = localStorage.getItem("theme") || "light";

const currentRadio = themeSelector.querySelector(
    `input[value="${savedTheme}"]`
);
if (currentRadio) {
    currentRadio.checked = true;
}

themeSelector.addEventListener("change", (e) => {
    const newTheme = e.target.value;
    document.body.setAttribute("data-theme", newTheme);
    localStorage.setItem("theme", newTheme);
    if (
    window.location.pathname.includes("/Dashboard.html") ||
    window.location.pathname === "/"
    ) {
    window.location.reload();
    }
});
}
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
