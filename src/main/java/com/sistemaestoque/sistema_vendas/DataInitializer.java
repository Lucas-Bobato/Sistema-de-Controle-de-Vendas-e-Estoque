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
        /*
        System.out.println(">>> Limpando dados antigos do banco de dados...");
        
        itemVendaRepository.deleteAllInBatch();
        vendaRepository.deleteAllInBatch();
        notificacaoRepository.deleteAllInBatch();
        produtoRepository.deleteAllInBatch();
        clienteRepository.deleteAllInBatch();
        usuarioRepository.deleteAllInBatch();
        
        System.out.println(">>> Dados antigos removidos com sucesso.");

        System.out.println(">>> Criando um novo conjunto de dados base...");
        Usuario admin = criarUsuario("Administrador", "admin@empresa.com", "admin123", UsuarioRole.ADMIN);
        Usuario vendedor = criarUsuario("Vendedor Teste", "vendedor@empresa.com", "vendedor123", UsuarioRole.VENDEDOR);
        Cliente clienteTech = criarCliente("Tech Solutions Ltda", "12.345.678/0001-99");
        Cliente clienteInova = criarCliente("Inova Corp S.A.", "98.765.432/0001-11");
        
        Produto p1 = criarProduto("Camisa de Algodão", "Vestuário", new BigDecimal("44.90"), new BigDecimal("25.00"), 50, 10);
        Produto p2 = criarProduto("Mochila Executiva", "Acessórios", new BigDecimal("149.90"), new BigDecimal("80.00"), 30, 5);
        Produto p3 = criarProduto("Fone Bluetooth Pro", "Eletrônicos", new BigDecimal("249.50"), new BigDecimal("150.00"), 20, 5);
        Produto p4 = criarProduto("Tênis Esportivo", "Calçados", new BigDecimal("199.85"), new BigDecimal("110.00"), 5, 8);
        List<Produto> todosProdutos = List.of(p1, p2, p3, p4);
        System.out.println(">>> Dados base criados com sucesso.");

        System.out.println(">>> Gerando vendas aleatórias para os últimos 7 dias...");
        LocalDate dataAtual = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate diaDaVenda = dataAtual.minusDays(i);
            int numeroDeVendasNoDia = ThreadLocalRandom.current().nextInt(1, 4);
            for(int j = 0; j < numeroDeVendasNoDia; j++) {
                Cliente clienteDaVez = (j % 2 == 0) ? clienteTech : clienteInova;
                Usuario vendedorDaVez = (j % 2 == 0) ? admin : vendedor;
                criarVendaAleatoria(diaDaVenda, clienteDaVez, vendedorDaVez, todosProdutos);
            }
        }
        System.out.println(">>> Inicialização de dados finalizada com sucesso.");
        */
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