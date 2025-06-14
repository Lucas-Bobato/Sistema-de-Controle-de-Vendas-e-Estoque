package com.sistemaestoque.sistema_vendas;

import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.model.UsuarioRole;
import com.sistemaestoque.sistema_vendas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByEmail("admin@empresa.com").isEmpty()) {
            
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@empresa.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setRole(UsuarioRole.ADMIN); // Perfil de Administrador
            admin.setAtivo(true);

            usuarioRepository.save(admin);
            System.out.println(">>> Usuário Administrador criado com sucesso!");
        }

        if (usuarioRepository.findByEmail("teste@empresa.com").isEmpty()) {

            Usuario teste = new Usuario();
            teste.setNome("Usuário de Teste");
            teste.setEmail("teste@empresa.com");
            teste.setSenha(passwordEncoder.encode("teste123"));
            teste.setRole(UsuarioRole.VENDEDOR); // Perfil de Vendedor
            teste.setAtivo(true);

            usuarioRepository.save(teste);
            System.out.println(">>> Usuário de Teste (Vendedor) criado com sucesso!");
        }
    }
}