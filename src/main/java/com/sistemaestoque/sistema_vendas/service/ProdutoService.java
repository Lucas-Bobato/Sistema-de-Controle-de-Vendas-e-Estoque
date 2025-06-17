package com.sistemaestoque.sistema_vendas.service;

import com.sistemaestoque.sistema_vendas.model.Produto;
import com.sistemaestoque.sistema_vendas.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarTodos(Sort sort) {
        return produtoRepository.findAll(sort);
    }

    public List<Produto> buscar(String termo, Sort sort) {
        if (termo == null || termo.trim().isEmpty()) {
            return listarTodos(sort);
        }
        return produtoRepository.findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCase(termo, termo, sort);
    }

    public void salvar(Produto produto) {
        produtoRepository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        return produto.orElse(null);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    // MÉTODOS PARA A PÁGINA DE ESTOQUE
    public Long getTotalItensEmEstoque() {
        Long total = produtoRepository.sumQuantidadeEstoque();
        return total != null ? total : 0L;
    }

    public long getContagemEstoqueCritico() {
        return produtoRepository.countEstoqueCritico();
    }

    public long getContagemSemEstoque() {
        return produtoRepository.countSemEstoque();
    }

    public List<Produto> listarProdutosComEstoqueCritico() {
        return produtoRepository.findAll().stream()
            .filter(p -> p.getQuantidadeEstoque() <= p.getEstoqueMinimo())
            .collect(Collectors.toList());
    }
}