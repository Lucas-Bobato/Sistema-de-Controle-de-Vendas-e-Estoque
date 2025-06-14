package com.sistemaestoque.sistema_vendas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProdutoMaisVendidoDTO {
    private String nomeProduto;
    private Long quantidadeVendida;
}