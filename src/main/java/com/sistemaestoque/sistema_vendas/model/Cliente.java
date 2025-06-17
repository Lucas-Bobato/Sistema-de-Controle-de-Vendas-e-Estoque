package com.sistemaestoque.sistema_vendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A razão social é obrigatória.")
    @Size(min = 3, max = 100, message = "A razão social deve ter entre 3 e 100 caracteres.")
    private String razaoSocial;

    @Column(unique = true)
    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$", message = "Formato de CNPJ inválido. Use XX.XXX.XXX/XXXX-XX.")
    private String cnpj;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    private String email;

    // Permite que o telefone seja nulo, mas se preenchido, deve seguir o padrão
    @Pattern(regexp = "^$|^\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}$", message = "Formato de telefone inválido. Use (XX) XXXXX-XXXX.")
    private String telefone;

    @NotBlank(message = "O endereço é obrigatório.")
    @Size(min = 5, max = 200, message = "O endereço deve ter entre 5 e 200 caracteres.")
    private String endereco;
    
    private LocalDate dataCadastro;
}