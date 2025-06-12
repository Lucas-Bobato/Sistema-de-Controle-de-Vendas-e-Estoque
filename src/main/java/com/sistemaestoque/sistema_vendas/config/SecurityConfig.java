package com.sistemaestoque.sistema_vendas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/static/**", "/error").permitAll() // Permite acesso à tela de login e recursos estáticos
                .requestMatchers("/usuarios/**").hasRole("ADMIN") // Apenas ADMIN acessa a gestão de usuários
                .anyRequest().authenticated() // Todas as outras requisições exigem autenticação
            )
            .formLogin(form -> form
                .loginPage("/login") // Define a página de login customizada
                .defaultSuccessUrl("/Dashboard.html", true) // Redireciona para o dashboard após o login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout") // Redireciona para o login após o logout
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Desabilitar CSRF para simplificar (em produção, configure corretamente)
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}