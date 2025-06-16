package com.sistemaestoque.sistema_vendas.service;

import com.sistemaestoque.sistema_vendas.model.Venda;
import com.sistemaestoque.sistema_vendas.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    public List<Venda> listarTodas(Sort sort) {
        return vendaRepository.findAll(sort);
    }

    public void salvar(Venda venda) {
        // Aqui entraria a lógica de negócio, como por exemplo,
        // dar baixa no estoque dos produtos vendidos.
        vendaRepository.save(venda);
    }
    
    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        vendaRepository.deleteById(id);
    }
    
    public List<Venda> buscar(String termo) {
        return vendaRepository.findByClienteRazaoSocialContainingIgnoreCaseOrVendedorNomeContainingIgnoreCase(termo, termo);
    }
    public List<Venda> buscar(String termo, Sort sort) {
        return vendaRepository.findByClienteRazaoSocialContainingIgnoreCaseOrVendedorNomeContainingIgnoreCase(termo, termo, sort);
    }
}