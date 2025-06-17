package com.sistemaestoque.sistema_vendas.repository;

import com.sistemaestoque.sistema_vendas.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Sort;
import java.util.Optional;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.quantidadeEstoque <= p.estoqueMinimo")
    long countEstoqueCritico();

    @Query("SELECT SUM(p.quantidadeEstoque) FROM Produto p")
    Long sumQuantidadeEstoque();

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.quantidadeEstoque <= 0")
    long countSemEstoque();
    
    // NOVO MÉTODO
    List<Produto> findByQuantidadeEstoqueLessThanEqual(Integer estoqueMinimo);

    Optional<Produto> findByNome(String nome);

    List<Produto> findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCase(String nome, String categoria, Sort sort);
}