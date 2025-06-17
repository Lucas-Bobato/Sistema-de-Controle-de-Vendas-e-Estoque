package com.sistemaestoque.sistema_vendas.service;

import com.sistemaestoque.sistema_vendas.model.Produto;
import com.sistemaestoque.sistema_vendas.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarTodos(Sort sort) {
        return produtoRepository.findAll(sort);
    }

    public List<Produto> buscar(String termo, Sort sort) {
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
}