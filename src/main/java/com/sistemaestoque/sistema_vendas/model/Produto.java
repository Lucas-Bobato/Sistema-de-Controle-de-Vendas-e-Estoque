package com.sistemaestoque.sistema_vendas.model;

import jakarta.persistence.*;
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
    private Integer quantidadeEstoque;
    private Integer estoqueMinimo;

    // Método para calcular o Lucro
    @Transient
    public BigDecimal getLucro() {
        if (preco == null || custo == null) {
            return BigDecimal.ZERO;
        }
        return preco.subtract(custo);
    }

    // Método para calcular a Margem
    @Transient
    public BigDecimal getMargem() {
        if (preco == null || custo == null || preco.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal lucro = getLucro();
        return lucro.multiply(new BigDecimal("100.0")).divide(preco, 2, RoundingMode.HALF_UP);
    }
}