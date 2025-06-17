package com.sistemaestoque.sistema_vendas.service;

import com.sistemaestoque.sistema_vendas.exception.CnpjDuplicadoException;
import com.sistemaestoque.sistema_vendas.model.Cliente;
import com.sistemaestoque.sistema_vendas.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos(Sort sort) {
        return clienteRepository.findAll(sort);
    }

    public List<Cliente> buscar(String termo, Sort sort) {
        return clienteRepository.findByRazaoSocialContainingIgnoreCaseOrCnpjContainingIgnoreCase(termo, termo, sort);
    }

public void salvar(Cliente cliente) {
    Optional<Cliente> clienteExistente = clienteRepository.findByCnpj(cliente.getCnpj());

    if (clienteExistente.isPresent() && !clienteExistente.get().getId().equals(cliente.getId())) {
        throw new CnpjDuplicadoException("Já existe um cliente cadastrado com o CNPJ: " + cliente.getCnpj());
    }

    if (cliente.getId() == null) {
        cliente.setDataCadastro(LocalDate.now());
    }
    clienteRepository.save(cliente);
}

    public Cliente buscarPorId(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.orElse(null);
    }

    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }
}