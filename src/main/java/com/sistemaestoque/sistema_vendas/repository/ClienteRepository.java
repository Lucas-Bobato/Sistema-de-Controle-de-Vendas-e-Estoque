package com.sistemaestoque.sistema_vendas.repository;

import com.sistemaestoque.sistema_vendas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Conta quantos clientes foram cadastrados em uma data específica
    long countByDataCadastro(LocalDate dataCadastro);
}