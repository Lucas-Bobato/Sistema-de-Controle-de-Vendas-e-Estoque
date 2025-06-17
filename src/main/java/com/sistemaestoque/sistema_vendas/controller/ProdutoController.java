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
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // MÉTODO ADICIONADO PARA CORRIGIR O ERRO 404
    @GetMapping("/estoque")
    public String paginaEstoque(Model model) {
        model.addAttribute("totalItens", produtoService.getTotalItensEmEstoque());
        model.addAttribute("estoqueCritico", produtoService.getContagemEstoqueCritico());
        model.addAttribute("semEstoque", produtoService.getContagemSemEstoque());
        model.addAttribute("produtosCriticos", produtoService.listarProdutosComEstoqueCritico());
        model.addAttribute("todosProdutos", produtoService.listarTodos(Sort.by("id")));
        return "Estoque";
    }

    @GetMapping
    public String listarProdutos(@RequestParam(required = false) String termo,
                                @RequestParam(defaultValue = "id") String sortField,
                                @RequestParam(defaultValue = "asc") String sortDir,
                                Model model) {

        List<Produto> produtos;
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        if (sortField.equals("lucro") || sortField.equals("margem")) {
            produtos = produtoService.buscar(termo, Sort.unsorted()); 
            
            Comparator<Produto> comparator = sortField.equals("lucro")
                ? Comparator.comparing(Produto::getLucro)
                : Comparator.comparing(Produto::getMargem);

            if (direction == Sort.Direction.DESC) {
                comparator = comparator.reversed();
            }
            produtos.sort(comparator);

        } else {
            Sort sort = Sort.by(direction, sortField);
            produtos = produtoService.buscar(termo, sort);
        }

        model.addAttribute("produtos", produtos);
        model.addAttribute("termo", termo);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "Produtos";
    }
    
    // ...outros métodos (novo, editar, salvar, etc. permanecem os mesmos)...
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
            writer.println("ID,NOME,CATEGORIA,PRECO,CUSTO,ESTOQUE_ATUAL,ESTOQUE_MINIMO");

            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            List<Produto> produtos = produtoService.listarTodos(sort);

            for (Produto produto : produtos) {
                writer.println(String.format("%d,%s,%s,%.2f,%.2f,%d,%d",
                        produto.getId(),
                        produto.getNome(),
                        produto.getCategoria(),
                        produto.getPreco(),
                        produto.getCusto(),
                        produto.getQuantidadeEstoque(),
                        produto.getEstoqueMinimo()));
            }
        }
    }
}