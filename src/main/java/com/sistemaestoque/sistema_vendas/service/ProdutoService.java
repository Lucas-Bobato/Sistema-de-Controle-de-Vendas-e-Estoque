package com.sistemaestoque.sistema_vendas.service;

import com.sistemaestoque.sistema_vendas.model.Notificacao;
import com.sistemaestoque.sistema_vendas.model.Produto;
import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.model.UsuarioRole;
import com.sistemaestoque.sistema_vendas.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private UsuarioService usuarioService;

    public List<Produto> listarTodos(Sort sort) {
        return produtoRepository.findAll(sort);
    }

    public List<Produto> buscar(String termo, Sort sort) {
        if (termo == null || termo.trim().isEmpty()) {
            return listarTodos(sort);
        }
        return produtoRepository.findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCase(termo, termo, sort);
    }

    public void salvar(Produto produto) {
        produtoRepository.save(produto);

        if (produto.getQuantidadeEstoque() <= produto.getEstoqueMinimo()) {
            String mensagem = "O produto '" + produto.getNome() + "' atingiu o nível de estoque crítico.";
            List<Usuario> admins = usuarioService.listarTodos(Sort.by("id"))
                    .stream()
                    .filter(u -> u.getRole() == UsuarioRole.ADMIN)
                    .collect(Collectors.toList());

            for (Usuario admin : admins) {
                notificacaoService.criarNotificacao(admin, mensagem, Notificacao.TipoNotificacao.ESTOQUE_CRITICO);
            }
        }
    }

    public Produto buscarPorId(Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        return produto.orElse(null);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    // MÉTODOS PARA A PÁGINA DE ESTOQUE
    public Long getTotalItensEmEstoque() {
        Long total = produtoRepository.sumQuantidadeEstoque();
        return total != null ? total : 0L;
    }

    public long getContagemEstoqueCritico() {
        return produtoRepository.countEstoqueCritico();
    }

    public long getContagemSemEstoque() {
        return produtoRepository.countSemEstoque();
    }

    public List<Produto> listarProdutosComEstoqueCritico() {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getQuantidadeEstoque() <= p.getEstoqueMinimo())
                .collect(Collectors.toList());
    }
}