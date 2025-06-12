package com.sistemaestoque.sistema_vendas;

// Importações necessárias para o código funcionar
import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.model.UsuarioRole;
import com.sistemaestoque.sistema_vendas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * A anotação @Component diz ao Spring para gerenciar esta classe.
 * A implementação de CommandLineRunner faz com que o método run()
 * seja executado automaticamente assim que a aplicação iniciar.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    // @Autowired injeta a dependência do nosso repositório de usuários
    @Autowired
    private UsuarioRepository usuarioRepository;

    // @Autowired injeta a dependência do codificador de senhas que configuramos
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Esta linha impede que o usuário seja criado toda vez que a aplicação reiniciar.
        // Ele só será criado se não houver nenhum usuário com o e-mail "admin@empresa.com".
        if (usuarioRepository.findByEmail("admin@empresa.com").isEmpty()) {
            
            // Criamos um novo objeto de usuário
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@empresa.com");

            // IMPORTANTE: Nunca salve senhas como texto puro. Usamos o passwordEncoder para criptografá-la.
            admin.setSenha(passwordEncoder.encode("admin123")); // A senha para login será 'admin123'
            
            admin.setRole(UsuarioRole.ADMIN);
            admin.setAtivo(true);

            // Salvamos o novo usuário administrador no banco de dados
            usuarioRepository.save(admin);

            // Imprimimos uma mensagem no console para confirmar que o usuário foi criado
            System.out.println(">>> Usuário Administrador criado com sucesso!");
        }
    }
}