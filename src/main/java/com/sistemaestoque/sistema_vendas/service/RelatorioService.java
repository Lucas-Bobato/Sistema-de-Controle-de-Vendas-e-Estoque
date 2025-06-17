package com.sistemaestoque.sistema_vendas.service;

import com.sistemaestoque.sistema_vendas.model.ItemVenda;
import com.sistemaestoque.sistema_vendas.model.Produto;
import com.sistemaestoque.sistema_vendas.model.Venda;
import com.sistemaestoque.sistema_vendas.repository.ItemVendaRepository;
import com.sistemaestoque.sistema_vendas.repository.ProdutoRepository;
import com.sistemaestoque.sistema_vendas.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private ItemVendaRepository itemVendaRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    public List<List<Object>> gerarRelatorioVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<Venda> vendas = vendaRepository.findByDataVendaBetween(dataInicio, dataFim, Sort.by("dataVenda"));
        return vendas.stream()
                .map(venda -> List.of(
                        (Object) ("#" + venda.getId()),
                        venda.getDataVenda(),
                        venda.getCliente().getRazaoSocial(),
                        venda.getVendedor().getNome(),
                        String.format("R$ %.2f", venda.getValorTotal())
                ))
                .collect(Collectors.toList());
    }

    public List<List<Object>> gerarRelatorioProdutosMaisVendidos() {
        return itemVendaRepository.findTop5ProdutosMaisVendidos().stream()
                .map(dto -> List.of(
                        (Object) dto.getNomeProduto(),
                        dto.getQuantidadeVendida()
                ))
                .collect(Collectors.toList());
    }
    
    public List<List<Object>> gerarRelatorioEstoqueCritico() {
        List<Produto> produtos = produtoRepository.findAll(Sort.by("nome"));
        return produtos.stream()
                .filter(p -> p.getQuantidadeEstoque() <= p.getEstoqueMinimo())
                .map(p -> List.of(
                        (Object) ("#PROD" + p.getId()),
                        p.getNome(),
                        p.getQuantidadeEstoque(),
                        p.getEstoqueMinimo(),
                        (p.getQuantidadeEstoque() - p.getEstoqueMinimo())
                ))
                .collect(Collectors.toList());
    }

    public List<List<Object>> gerarRelatorioRentabilidade() {
        List<ItemVenda> todosItensVendidos = itemVendaRepository.findAll();
        return new ArrayList<>(todosItensVendidos.stream()
                .collect(Collectors.groupingBy(item -> item.getProduto()))
                .entrySet().stream()
                .map(entry -> {
                    Produto produto = entry.getKey();
                    List<ItemVenda> itens = entry.getValue();
                    
                    int qtdTotalVendida = itens.stream().mapToInt(ItemVenda::getQuantidade).sum();
                    
                    // Verificação de nulidade para o custo
                    BigDecimal custoProduto = Optional.ofNullable(produto.getCusto()).orElse(BigDecimal.ZERO);
                    BigDecimal custoTotal = custoProduto.multiply(BigDecimal.valueOf(qtdTotalVendida));
                    
                    BigDecimal vendaTotal = itens.stream()
                            .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                            
                    BigDecimal lucroBruto = vendaTotal.subtract(custoTotal);
                    BigDecimal margem = (vendaTotal.compareTo(BigDecimal.ZERO) == 0) ? BigDecimal.ZERO :
                            lucroBruto.divide(vendaTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

                    return List.of(
                        (Object) produto.getNome(),
                        String.format("R$ %.2f", custoTotal),
                        String.format("R$ %.2f", vendaTotal),
                        String.format("R$ %.2f", lucroBruto),
                        String.format("%.2f%%", margem)
                    );
                }).collect(Collectors.toList()));
    }
}