package com.sistemaestoque.sistema_vendas;

import com.sistemaestoque.sistema_vendas.model.*;
import com.sistemaestoque.sistema_vendas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private ItemVendaRepository itemVendaRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Override
public void run(String... args) throws Exception {
    // Verifica se o usuário admin já existe antes de criar
    if (usuarioRepository.findByEmail("admin@empresa.com").isEmpty()) {
        System.out.println(">>> Criando usuário ADMIN inicial...");
        Usuario admin = new Usuario();
        admin.setNome("Administrador");
        admin.setEmail("admin@empresa.com");
        admin.setSenha(passwordEncoder.encode("admin123")); // Escolha uma senha forte aqui!
        admin.setRole(UsuarioRole.ADMIN);
        admin.setAtivo(true);
        usuarioRepository.save(admin);
        System.out.println(">>> Usuário ADMIN criado com sucesso!");
    } else {
        System.out.println(">>> Usuário ADMIN já existe. Nenhum usuário foi criado.");
    }
}

    private void criarVendaAleatoria(LocalDate data, Cliente cliente, Usuario vendedor, List<Produto> produtos) {
        int numeroDeTiposDeProduto = ThreadLocalRandom.current().nextInt(1, 4);
        List<ItemVenda> itens = new ArrayList<>();

        for (int i = 0; i < numeroDeTiposDeProduto; i++) {
            Produto produtoAleatorio = produtos.get(ThreadLocalRandom.current().nextInt(produtos.size()));
            int quantidadeAleatoria = ThreadLocalRandom.current().nextInt(1, 6);
            itens.add(criarItem(produtoAleatorio, quantidadeAleatoria));
        }
        
        criarVenda(cliente, vendedor, data, itens.toArray(new ItemVenda[0]));
    }

    private Usuario criarUsuario(String nome, String email, String senha, UsuarioRole role) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setRole(role);
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    private Cliente criarCliente(String razaoSocial, String cnpj) {
        Cliente cliente = new Cliente();
        cliente.setRazaoSocial(razaoSocial);
        cliente.setCnpj(cnpj);
        String emailDomain = razaoSocial.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        cliente.setEmail("contato@" + emailDomain + ".com");
        cliente.setTelefone("(11) 98888-7777");
        cliente.setEndereco("Endereço Padrão");
        cliente.setDataCadastro(LocalDate.now());
        return clienteRepository.save(cliente);
    }

    private Produto criarProduto(String nome, String categoria, BigDecimal preco, BigDecimal custo, int estoque, int estoqueMinimo) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setCategoria(categoria);
        produto.setPreco(preco);
        produto.setCusto(custo);
        produto.setQuantidadeEstoque(estoque);
        produto.setEstoqueMinimo(estoqueMinimo);
        return produtoRepository.save(produto);
    }

    private ItemVenda criarItem(Produto produto, int quantidade) {
        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        return item;
    }

    private void criarVenda(Cliente cliente, Usuario vendedor, LocalDate data, ItemVenda... itens) {
        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setVendedor(vendedor);
        venda.setDataVenda(data);
        venda.setStatus("Concluido");

        List<ItemVenda> listaItens = Arrays.asList(itens);
        venda.setItens(listaItens);
        listaItens.forEach(item -> item.setVenda(venda));

        BigDecimal valorTotal = listaItens.stream()
                .map(item -> item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        venda.setValorTotal(valorTotal);

        vendaRepository.save(venda);
    }
}