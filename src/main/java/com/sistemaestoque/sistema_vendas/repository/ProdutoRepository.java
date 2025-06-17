package com.sistemaestoque.sistema_vendas.repository;

import com.sistemaestoque.sistema_vendas.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Sort;
import java.util.Optional;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Conta quantos produtos estão com o estoque atual abaixo ou igual ao mínimo
    @Query("SELECT COUNT(p) FROM Produto p WHERE p.quantidadeEstoque <= p.estoqueMinimo")
    long countEstoqueCritico();

    Optional<Produto> findByNome(String nome);

    List<Produto> findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCase(String nome, String categoria, Sort sort);
}