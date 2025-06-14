package com.sistemaestoque.sistema_vendas.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String razaoSocial;
    private String cnpj;
    private String email;
    private String telefone;
    private String endereco;
    private LocalDate dataCadastro;
}