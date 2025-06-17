package com.sistemaestoque.sistema_vendas.service;

import com.sistemaestoque.sistema_vendas.model.ItemVenda;
import com.sistemaestoque.sistema_vendas.model.Produto;
import com.sistemaestoque.sistema_vendas.model.Venda;
import com.sistemaestoque.sistema_vendas.repository.ProdutoRepository;
import com.sistemaestoque.sistema_vendas.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Venda> listarTodas(Sort sort) {
        return vendaRepository.findAll(sort);
    }

    @Transactional
    public void salvar(Venda venda) {
        // Se a venda já existe (é uma atualização), restaura o estoque antigo primeiro.
        if (venda.getId() != null) {
            vendaRepository.findById(venda.getId()).ifPresent(vendaAntiga -> {
                for (ItemVenda item : vendaAntiga.getItens()) {
                    Produto produto = item.getProduto();
                    produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
                    produtoRepository.save(produto);
                }
            });
        }

        // Debita o estoque para os novos itens da venda.
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            int quantidadeVendida = item.getQuantidade();
            
            // Verifica se há estoque suficiente antes de debitar.
            if (produto.getQuantidadeEstoque() < quantidadeVendida) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidadeVendida);
            produtoRepository.save(produto);
        }

        vendaRepository.save(venda);
    }
    
    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deletar(Long id) {
        // Antes de deletar a venda, restaura a quantidade de itens ao estoque.
        vendaRepository.findById(id).ifPresent(venda -> {
            for (ItemVenda item : venda.getItens()) {
                Produto produto = item.getProduto();
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
                produtoRepository.save(produto);
            }
        });
        
        vendaRepository.deleteById(id);
    }
    
    public List<Venda> buscar(String termo) {
        return vendaRepository.findByClienteRazaoSocialContainingIgnoreCaseOrVendedorNomeContainingIgnoreCase(termo, termo);
    }
    
    public List<Venda> buscar(String termo, Sort sort) {
        return vendaRepository.findByClienteRazaoSocialContainingIgnoreCaseOrVendedorNomeContainingIgnoreCase(termo, termo, sort);
    }
}