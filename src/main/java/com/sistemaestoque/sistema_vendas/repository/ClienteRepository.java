package com.sistemaestoque.sistema_vendas.repository;

import com.sistemaestoque.sistema_vendas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Conta quantos clientes foram cadastrados em uma data específica
    long countByDataCadastro(LocalDate dataCadastro);

    Optional<Cliente> findByCnpj(String cnpj);

    List<Cliente> findByRazaoSocialContainingIgnoreCaseOrCnpjContainingIgnoreCase(String razaoSocial, String cnpj, Sort sort);
}