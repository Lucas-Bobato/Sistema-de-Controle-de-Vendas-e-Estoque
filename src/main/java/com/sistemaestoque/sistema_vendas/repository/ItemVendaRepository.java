package com.sistemaestoque.sistema_vendas.repository;

import com.sistemaestoque.sistema_vendas.dto.ProdutoMaisVendidoDTO;
import com.sistemaestoque.sistema_vendas.model.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {

    @Query("SELECT new com.sistemaestoque.sistema_vendas.dto.ProdutoMaisVendidoDTO(iv.produto.nome, SUM(iv.quantidade)) " +
            "FROM ItemVenda iv " +
            "GROUP BY iv.produto.nome " +
            "ORDER BY SUM(iv.quantidade) DESC")
    List<ProdutoMaisVendidoDTO> findTop5ProdutosMaisVendidos();
}