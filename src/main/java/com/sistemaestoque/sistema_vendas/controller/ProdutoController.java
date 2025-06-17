package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.model.Produto;
import com.sistemaestoque.sistema_vendas.service.ProdutoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listarProdutos(@RequestParam(required = false) String termo,
                                @RequestParam(defaultValue = "id") String sortField,
                                @RequestParam(defaultValue = "asc") String sortDir,
                                Model model) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        List<Produto> produtos;

        if (termo != null && !termo.isEmpty()) {
            produtos = produtoService.buscar(termo, sort);
        } else {
            produtos = produtoService.listarTodos(sort);
        }

        model.addAttribute("produtos", produtos);
        model.addAttribute("termo", termo);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "Produtos";
    }

    @GetMapping("/novo")
    public String novoProdutoForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "formProduto";
    }

    @GetMapping("/editar/{id}")
    public String editarProdutoForm(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id);
        model.addAttribute("produto", produto);
        return "formProduto";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto) {
        produtoService.salvar(produto);
        return "redirect:/produtos";
    }

    @GetMapping("/deletar/{id}")
    public String confirmarDelecao(@PathVariable Long id, Model model) {
        Produto produto = produtoService.buscarPorId(id);
        model.addAttribute("produto", produto);
        return "confirmarDelecaoProduto";
    }

    @PostMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable Long id) {
        produtoService.deletar(id);
        return "redirect:/produtos";
    }

    @GetMapping("/exportar/csv")
    public void exportarCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; file=relatorio-produtos.csv");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("ID,NOME,CATEGORIA,PRECO,ESTOQUE_ATUAL,ESTOQUE_MINIMO");

            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            List<Produto> produtos = produtoService.listarTodos(sort);

            for (Produto produto : produtos) {
                writer.println(String.format("%d,%s,%s,%.2f,%d,%d",
                        produto.getId(),
                        produto.getNome(),
                        produto.getCategoria(),
                        produto.getPreco(),
                        produto.getQuantidadeEstoque(),
                        produto.getEstoqueMinimo()));
            }
        }
    }
}