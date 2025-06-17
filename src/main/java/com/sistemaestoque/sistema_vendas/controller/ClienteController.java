package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.exception.CnpjDuplicadoException;
import com.sistemaestoque.sistema_vendas.model.Cliente;
import com.sistemaestoque.sistema_vendas.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // ... (métodos listarClientes, novoClienteForm, editarClienteForm não precisam de mudança)
    @GetMapping
    public String listarClientes(@RequestParam(required = false) String termo,
                                @RequestParam(defaultValue = "id") String sortField,
                                @RequestParam(defaultValue = "asc") String sortDir,
                                Model model) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        List<Cliente> clientes;

        if (termo != null && !termo.isEmpty()) {
            clientes = clienteService.buscar(termo, sort);
        } else {
            clientes = clienteService.listarTodos(sort);
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("termo", termo);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "Clientes";
    }

    @GetMapping("/novo")
    public String novoClienteForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "formCliente";
    }

    @GetMapping("/editar/{id}")
    public String editarClienteForm(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        model.addAttribute("cliente", cliente);
        return "formCliente";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@Valid @ModelAttribute("cliente") Cliente cliente,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "formCliente";
        }

        try {
            clienteService.salvar(cliente);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente salvo com sucesso!");
        } catch (CnpjDuplicadoException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            if (cliente.getId() == null) {
                return "redirect:/clientes/novo";
            } else {
                return "redirect:/clientes/editar/" + cliente.getId();
            }
        }
        return "redirect:/clientes";
    }
    
    @GetMapping("/deletar/{id}")
    public String confirmarDelecao(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        model.addAttribute("cliente", cliente);
        return "confirmarDelecaoCliente";
    }

    @PostMapping("/deletar/{id}")
    public String deletarCliente(@PathVariable Long id) {
        clienteService.deletar(id);
        return "redirect:/clientes";
    }
}