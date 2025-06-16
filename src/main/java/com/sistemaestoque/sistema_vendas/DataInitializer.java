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

    @Override
    public void run(String... args) throws Exception {
        
        // Limpa os dados de teste para evitar encher o banco com dados que não serão usados
        System.out.println(">>> Limpando dados antigos do banco de dados...");
        itemVendaRepository.deleteAllInBatch();
        vendaRepository.deleteAllInBatch();
        produtoRepository.deleteAllInBatch();
        clienteRepository.deleteAllInBatch();
        usuarioRepository.deleteAllInBatch();
        System.out.println(">>> Dados antigos removidos com sucesso.");

        // Cria um novo conjunto de dados para teste
        System.out.println(">>> Criando um novo conjunto de dados base...");
        Usuario admin = criarUsuario("Administrador", "admin@empresa.com", "admin123", UsuarioRole.ADMIN);
        Usuario vendedor = criarUsuario("Vendedor Teste", "vendedor@empresa.com", "vendedor123", UsuarioRole.VENDEDOR);
        Cliente clienteTech = criarCliente("Tech Solutions Ltda", "12.345.678/0001-99");
        Cliente clienteInova = criarCliente("Inova Corp S.A.", "98.765.432/0001-11");
        Produto p1 = criarProduto("Camisa de Algodão", "Vestuário", new BigDecimal("44.90"), 50, 10);
        Produto p2 = criarProduto("Mochila Executiva", "Acessórios", new BigDecimal("149.90"), 30, 5);
        Produto p3 = criarProduto("Fone Bluetooth Pro", "Eletrônicos", new BigDecimal("249.50"), 20, 5);
        Produto p4 = criarProduto("Tênis Esportivo", "Calçados", new BigDecimal("199.85"), 5, 8);
        List<Produto> todosProdutos = List.of(p1, p2, p3, p4);
        System.out.println(">>> Dados base criados com sucesso.");

        // Gere dados de vendas aleatórias para os últimos 7 dias
        System.out.println(">>> Gerando vendas aleatórias para os últimos 7 dias...");
        LocalDate dataAtual = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate diaDaVenda = dataAtual.minusDays(i);
            // Cria entre 1 e 3 vendas para cada dia, para dar mais volume
            int numeroDeVendasNoDia = ThreadLocalRandom.current().nextInt(1, 4);
            for(int j = 0; j < numeroDeVendasNoDia; j++) {
                // Alterna o cliente para diversificar
                Cliente clienteDaVez = (j % 2 == 0) ? clienteTech : clienteInova;
                Usuario vendedorDaVez = (j % 2 == 0) ? admin : vendedor;
                criarVendaAleatoria(diaDaVenda, clienteDaVez, vendedorDaVez, todosProdutos);
            }
        }
        System.out.println(">>> Inicialização de dados finalizada com sucesso.");
    }

    private void criarVendaAleatoria(LocalDate data, Cliente cliente, Usuario vendedor, List<Produto> produtos) {
        int numeroDeTiposDeProduto = ThreadLocalRandom.current().nextInt(1, 4); // Vende de 1 a 3 tipos de produto
        List<ItemVenda> itens = new ArrayList<>();

        for (int i = 0; i < numeroDeTiposDeProduto; i++) {
            Produto produtoAleatorio = produtos.get(ThreadLocalRandom.current().nextInt(produtos.size()));
            int quantidadeAleatoria = ThreadLocalRandom.current().nextInt(1, 6); // Vende de 1 a 5 unidades
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
        cliente.setEmail("contato@" + razaoSocial.replaceAll("\\s+", "").toLowerCase() + ".com");
        cliente.setTelefone("(11) 98888-7777");
        cliente.setEndereco("Endereço Padrão");
        cliente.setDataCadastro(LocalDate.now());
        return clienteRepository.save(cliente);
    }

    private Produto criarProduto(String nome, String categoria, BigDecimal preco, int estoque, int estoqueMinimo) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setCategoria(categoria);
        produto.setPreco(preco);
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