package com.sistemaestoque.sistema_vendas.service;

import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos(Sort sort) {
        return usuarioRepository.findAll(sort);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void salvar(Usuario usuario) {
      // A lógica de verificação de e-mail e senha é tratada no Controller.
      usuarioRepository.save(usuario);
  }

    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado para deleção.");
        }
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorEmail(String email) {
      return usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
  }
  
  public void atualizarPerfil(Usuario usuario, String nome, String email, String novaSenha) {
    usuario.setNome(nome);
    usuario.setEmail(email);

    if (novaSenha != null && !novaSenha.isEmpty()) {
        usuario.setSenha(passwordEncoder.encode(novaSenha));
    }
    
    usuarioRepository.save(usuario);

  }
    public Optional<Usuario> buscarPorEmailOptional(String email) {
    return usuarioRepository.findByEmail(email);
  }
}