package com.sistemaestoque.sistema_vendas.repository;

import com.sistemaestoque.sistema_vendas.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Conta quantos produtos estão com o estoque atual abaixo ou igual ao mínimo
    @Query("SELECT COUNT(p) FROM Produto p WHERE p.quantidadeEstoque <= p.estoqueMinimo")
    long countEstoqueCritico();
}