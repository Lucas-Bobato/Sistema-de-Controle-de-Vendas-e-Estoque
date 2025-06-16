package com.sistemaestoque.sistema_vendas.repository;

import com.sistemaestoque.sistema_vendas.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    @Query("SELECT SUM(v.valorTotal) FROM Venda v WHERE v.dataVenda = :data")
    Optional<BigDecimal> findTotalVendasByData(@Param("data") LocalDate data);

    @Query("SELECT SUM(iv.quantidade) FROM Venda v JOIN v.itens iv WHERE v.dataVenda = :data")
    Optional<Long> findTotalProdutosVendidosByData(@Param("data") LocalDate data);

    List<Venda> findTop5ByOrderByDataVendaDesc();

    List<Venda> findByClienteRazaoSocialContainingIgnoreCaseOrVendedorNomeContainingIgnoreCase(String razaoSocial, String nomeVendedor);

    List<Venda> findByClienteRazaoSocialContainingIgnoreCaseOrVendedorNomeContainingIgnoreCase(String razaoSocial, String nomeVendedor, Sort sort);
}