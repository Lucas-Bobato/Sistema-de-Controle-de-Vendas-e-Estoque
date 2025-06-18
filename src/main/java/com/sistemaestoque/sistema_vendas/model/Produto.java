package com.sistemaestoque.sistema_vendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
@Table(name = "produtos")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String categoria;
    private BigDecimal preco;
    private BigDecimal custo;

    @NotNull(message = "A quantidade em estoque é obrigatória.")
    private Integer quantidadeEstoque;
    
    @NotNull(message = "O estoque mínimo é obrigatório.")
    private Integer estoqueMinimo;

    @Transient
    public BigDecimal getLucro() {
        if (preco == null || custo == null) {
            return BigDecimal.ZERO;
        }
        return preco.subtract(custo);
    }

    @Transient
    public BigDecimal getMargem() {
        if (preco == null || custo == null || preco.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal lucro = getLucro();
        return lucro.multiply(new BigDecimal("100.0")).divide(preco, 2, RoundingMode.HALF_UP);
    }
}