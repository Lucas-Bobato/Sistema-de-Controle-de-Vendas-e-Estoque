package com.sistemaestoque.sistema_vendas.service;

import com.sistemaestoque.sistema_vendas.exception.EstoqueInsuficienteException;
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
import java.util.Optional;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoService produtoService;

    public List<Venda> listarTodas(Sort sort) {
        return vendaRepository.findAll(sort);
    }

    @Transactional
    public void salvar(Venda venda) {
        if (venda.getId() != null) {
            vendaRepository.findById(venda.getId()).ifPresent(vendaAntiga -> {
                for (ItemVenda item : vendaAntiga.getItens()) {
                    Produto produto = item.getProduto();
                    produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
                    produtoRepository.save(produto);
                }
            });
        }

        if (venda.getItens() != null) {
            for (ItemVenda item : venda.getItens()) {
                item.setVenda(venda);
            }
        }

        for (ItemVenda item : venda.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + item.getProduto().getId()));
            
            item.setProduto(produto);

            int quantidadeVendida = item.getQuantidade();
            int estoqueDisponivel = Optional.ofNullable(produto.getQuantidadeEstoque()).orElse(0);
            
            if (estoqueDisponivel < quantidadeVendida) {
                String mensagem = String.format(
                    "Estoque insuficiente para o produto: '%s'. Disponível: %d, Tentativa de venda: %d",
                    produto.getNome(),
                    estoqueDisponivel,
                    quantidadeVendida
                );
                throw new EstoqueInsuficienteException(mensagem);
            }
            
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidadeVendida);
            produtoService.salvar(produto);
        }

        vendaRepository.save(venda);
    }
    
    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deletar(Long id) {
        vendaRepository.findById(id).ifPresent(venda -> {
            for (ItemVenda item : venda.getItens()) {
                Produto produto = item.getProduto();
                int estoqueAtual = Optional.ofNullable(produto.getQuantidadeEstoque()).orElse(0);
                produto.setQuantidadeEstoque(estoqueAtual + item.getQuantidade());
                produtoRepository.save(produto);
            }
        });
        
        vendaRepository.deleteById(id);
    }
    
    public List<Venda> buscar(String termo, Sort sort) {
        return vendaRepository.findByClienteRazaoSocialContainingIgnoreCaseOrVendedorNomeContainingIgnoreCase(termo, termo, sort);
    }
  }