package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/perfil")
    public String perfilUsuario(Model model, @AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioService.buscarPorEmail(principal.getUsername());
        model.addAttribute("usuario", usuario);
        return "perfil";
    }
    
    @PostMapping("/perfil/salvar")
    public String salvarPerfil(@ModelAttribute Usuario usuarioDoForm,
                              @RequestParam(name = "senha", required = false) String novaSenha,
                              @RequestParam("foto") MultipartFile foto,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal UserDetails principal) throws IOException {
        
        if (principal == null) {
            return "redirect:/login";
        }
    
        try {
            Usuario usuarioLogado = usuarioService.buscarPorEmail(principal.getUsername());
    
            // Lógica para salvar a foto
            if (foto != null && !foto.isEmpty()) {
                String fileName = foto.getOriginalFilename(); // Obter o nome do arquivo
    
                // Adicionar verificação de nulidade
                if (fileName != null) {
                    fileName = StringUtils.cleanPath(fileName);
                    usuarioLogado.setFotoPerfil(fileName);
    
                    String uploadDir = "src/main/resources/static/images/perfil/";
                    Path uploadPath = Paths.get(uploadDir);
    
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
    
                    try (InputStream inputStream = foto.getInputStream()) {
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
    
            usuarioService.atualizarPerfil(usuarioLogado.getId(), usuarioDoForm.getNome(), usuarioDoForm.getEmail(), novaSenha);
            
            redirectAttributes.addFlashAttribute("successMessage", "Perfil atualizado com sucesso!");
    
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/perfil";
    }
}