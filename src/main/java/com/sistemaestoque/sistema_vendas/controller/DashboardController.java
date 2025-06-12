package com.sistemaestoque.sistema_vendas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping({"/", "/Dashboard.html"})
    public String dashboard() {
        return "Dashboard"; // Retorna Dashboard.html
    }
    
    @GetMapping("/Clientes.html")
    public String clientes() {
        return "Clientes"; // Retorna Clientes.html
    }

    @GetMapping("/Produtos.html")
    public String produtos() {
        return "Produtos"; // Retorna Produtos.html
    }
    
    @GetMapping("/Vendas.html")
    public String vendas() {
        return "Vendas"; // Retorna Vendas.html
    }
    
    @GetMapping("/Estoque.html")
    public String estoque() {
        return "Estoque"; // Retorna Estoque.html
    }

    @GetMapping("/Relatorios.html")
    public String relatorios() {
        return "Relatorios"; // Retorna Relatorios.html
    }

    @GetMapping("/Usuarios.html")
    public String usuarios() {
        return "Usuarios"; // Retorna Usuarios.html
    }
}