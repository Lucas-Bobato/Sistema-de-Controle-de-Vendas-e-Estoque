package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.dto.ProdutoMaisVendidoDTO;
import com.sistemaestoque.sistema_vendas.model.Venda;
import com.sistemaestoque.sistema_vendas.repository.ClienteRepository;
import com.sistemaestoque.sistema_vendas.repository.ItemVendaRepository;
import com.sistemaestoque.sistema_vendas.repository.ProdutoRepository;
import com.sistemaestoque.sistema_vendas.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private ItemVendaRepository itemVendaRepository;

    @GetMapping({"/", "/Dashboard.html"})
    public String dashboard(Model model) {
        LocalDate hoje = LocalDate.now();

        // Cards
        model.addAttribute("vendasHoje", String.format("R$ %.2f", vendaRepository.findTotalVendasByData(hoje).orElse(BigDecimal.ZERO)));
        model.addAttribute("novosClientesHoje", clienteRepository.countByDataCadastro(hoje));
        model.addAttribute("produtosVendidosHoje", vendaRepository.findTotalProdutosVendidosByData(hoje).orElse(0L));
        model.addAttribute("estoqueCritico", produtoRepository.countEstoqueCritico());

        // Tabela de Últimas Vendas
        List<Venda> ultimasVendas = vendaRepository.findTop5ByOrderByDataVendaDesc();
        model.addAttribute("ultimasVendas", ultimasVendas);
        
        return "Dashboard";
    }
    
    @GetMapping("/api/vendas/ultimos7dias")
    @ResponseBody
    public Map<String, Object> getVendasUltimos7Dias() {
        Map<String, Object> data = new LinkedHashMap<>();
        Map<String, BigDecimal> vendasPorDia = new LinkedHashMap<>();
        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate dataBusca = hoje.minusDays(i);
            BigDecimal total = vendaRepository.findTotalVendasByData(dataBusca).orElse(BigDecimal.ZERO);
            vendasPorDia.put(dataBusca.format(formatter), total);
        }

        data.put("labels", vendasPorDia.keySet());
        data.put("values", vendasPorDia.values());
        return data;
    }

    @GetMapping("/api/produtos/mais-vendidos")
    @ResponseBody
    public Map<String, Object> getProdutosMaisVendidos() {
        Map<String, Object> data = new LinkedHashMap<>();
        List<ProdutoMaisVendidoDTO> topProdutos = itemVendaRepository.findTop5ProdutosMaisVendidos();

        data.put("labels", topProdutos.stream().map(ProdutoMaisVendidoDTO::getNomeProduto).collect(Collectors.toList()));
        data.put("values", topProdutos.stream().map(ProdutoMaisVendidoDTO::getQuantidadeVendida).collect(Collectors.toList()));
        return data;
    }
}