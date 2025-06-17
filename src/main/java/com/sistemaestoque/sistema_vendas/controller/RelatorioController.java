package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping
    public String paginaRelatorios() {
        return "Relatorios";
    }

    @GetMapping("/vendas-por-periodo")
    public String relatorioVendasPorPeriodo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Model model) {

        model.addAttribute("tipoRelatorio", "Vendas por Período");
        if (dataInicio != null && dataFim != null) {
            model.addAttribute("resultados", relatorioService.gerarRelatorioVendasPorPeriodo(dataInicio, dataFim));
            model.addAttribute("colunas", List.of("ID Venda", "Data", "Cliente", "Vendedor", "Valor Total"));
        }
        return "Relatorios";
    }

    @GetMapping("/produtos-mais-vendidos")
    public String relatorioProdutosMaisVendidos(Model model) {
        model.addAttribute("tipoRelatorio", "Produtos Mais Vendidos");
        model.addAttribute("resultados", relatorioService.gerarRelatorioProdutosMaisVendidos());
        model.addAttribute("colunas", List.of("Produto", "Quantidade Vendida"));
        return "Relatorios";
    }
    
    @GetMapping("/estoque-critico")
    public String relatorioEstoqueCritico(Model model) {
        model.addAttribute("tipoRelatorio", "Estoque Crítico");
        model.addAttribute("resultados", relatorioService.gerarRelatorioEstoqueCritico());
        model.addAttribute("colunas", List.of("ID", "Produto", "Estoque Atual", "Estoque Mínimo", "Diferença"));
        return "Relatorios";
    }

    @GetMapping("/rentabilidade-produtos")
    public String relatorioRentabilidade(Model model) {
        model.addAttribute("tipoRelatorio", "Rentabilidade por Produto");
        model.addAttribute("resultados", relatorioService.gerarRelatorioRentabilidade());
        model.addAttribute("colunas", List.of("Produto", "Custo Total", "Venda Total", "Lucro Bruto", "Margem (%)"));
        return "Relatorios";
    }
}