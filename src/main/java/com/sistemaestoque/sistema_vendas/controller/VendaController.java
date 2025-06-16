package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.model.Cliente;
import com.sistemaestoque.sistema_vendas.model.Produto;
import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.model.Venda;
import com.sistemaestoque.sistema_vendas.repository.ClienteRepository;
import com.sistemaestoque.sistema_vendas.repository.ProdutoRepository;
import com.sistemaestoque.sistema_vendas.repository.UsuarioRepository;
import com.sistemaestoque.sistema_vendas.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listarVendas( @RequestParam(name = "termo", required = false) String termo,
                                @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                                @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
                                Model model) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortField);

        List<Venda> vendas;
        if (termo != null && !termo.isEmpty()) {
            vendas = vendaService.buscar(termo, sort);
        } else {
            vendas = vendaService.listarTodas(sort);
        }

        model.addAttribute("vendas", vendas);
        model.addAttribute("termo", termo);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "Vendas";
    }

    @GetMapping("/novo")
    public String novaVendaForm(Model model) {
        List<Cliente> clientes = clienteRepository.findAll();
        List<Produto> produtos = produtoRepository.findAll();
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("venda", new Venda());
        model.addAttribute("clientes", clientes);
        model.addAttribute("produtos", produtos);
        model.addAttribute("usuarios", usuarios);
        return "formVenda";
    }

    @PostMapping("/salvar")
    public String salvarVenda(@ModelAttribute Venda venda) {
        if (venda.getDataVenda() == null) {
            venda.setDataVenda(LocalDate.now());
        }
        vendaService.salvar(venda);
        return "redirect:/vendas";
    }

    @GetMapping("/editar/{id}")
    public String editarVendaForm(@PathVariable Long id, Model model) {
        Venda venda = vendaService.buscarPorId(id);
        if (venda == null) {
            return "redirect:/vendas";
        }
        List<Cliente> clientes = clienteRepository.findAll();
        List<Produto> produtos = produtoRepository.findAll();
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("venda", venda);
        model.addAttribute("clientes", clientes);
        model.addAttribute("produtos", produtos);
        model.addAttribute("usuarios", usuarios);
        return "formVenda";
    }

    @GetMapping("/deletar/{id}")
    public String confirmarDelecao(@PathVariable Long id, Model model) {
        Venda venda = vendaService.buscarPorId(id);
        if (venda == null) {
            return "redirect:/vendas";
        }
        model.addAttribute("venda", venda);
        return "confirmarDelecaoVenda";
    }

    @PostMapping("/deletar/{id}")
    public String deletarVenda(@PathVariable Long id) {
        vendaService.deletar(id);
        return "redirect:/vendas";
    }
}