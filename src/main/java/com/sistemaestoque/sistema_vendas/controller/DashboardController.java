package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.repository.ClienteRepository;
import com.sistemaestoque.sistema_vendas.repository.ProdutoRepository;
import com.sistemaestoque.sistema_vendas.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class DashboardController {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping({"/", "/Dashboard.html"})
    public String dashboard(Model model) {
        LocalDate hoje = LocalDate.now();

        // Card: Vendas Hoje
        BigDecimal vendasHoje = vendaRepository.findTotalVendasByData(hoje).orElse(BigDecimal.ZERO);
        model.addAttribute("vendasHoje", String.format("R$ %.2f", vendasHoje));

        // Card: Novos Clientes (hoje)
        long novosClientesHoje = clienteRepository.countByDataCadastro(hoje);
        model.addAttribute("novosClientesHoje", novosClientesHoje);

        // Card: Produtos Vendidos (hoje)
        long produtosVendidosHoje = vendaRepository.findTotalProdutosVendidosByData(hoje).orElse(0L);
        model.addAttribute("produtosVendidosHoje", produtosVendidosHoje);
        
        // Card: Estoque Crítico
        long estoqueCritico = produtoRepository.countEstoqueCritico();
        model.addAttribute("estoqueCritico", estoqueCritico);
        
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