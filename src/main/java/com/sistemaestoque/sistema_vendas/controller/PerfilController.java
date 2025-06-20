package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

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
    public String salvarPerfil(@ModelAttribute("usuario") Usuario usuarioDoForm,
                              @RequestParam(name = "senha", required = false) String novaSenha,
                              @RequestParam("foto") MultipartFile foto,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal UserDetails principal) {
        
        if (principal == null) {
            return "redirect:/login";
        }
    
        try {
            Usuario usuarioParaAtualizar = usuarioService.buscarPorEmail(principal.getUsername());
    
            if (foto != null && !foto.isEmpty()) {
                String uploadDir = "src/main/resources/static/images/perfil/";
                Path uploadPath = Paths.get(uploadDir);

                File diretorio = new File(uploadDir);
                File[] arquivosAntigos = diretorio.listFiles((dir, name) ->
                    name.startsWith("usuario_" + usuarioParaAtualizar.getId() + "."));
                if (arquivosAntigos != null) {
                    for (File arquivo : arquivosAntigos) {
                        arquivo.delete();
                    }
                }

                String originalFileName = foto.getOriginalFilename();
                if (originalFileName != null && !originalFileName.trim().isEmpty()) {
                    String cleanedFileName = StringUtils.cleanPath(originalFileName);
                    String fileExtension = Optional.of(cleanedFileName)
                                                  .filter(f -> f.contains("."))
                                                  .map(f -> f.substring(f.lastIndexOf(".")))
                                                  .orElse("");
                    String newFileName = "usuario_" + usuarioParaAtualizar.getId() + fileExtension;
                    usuarioParaAtualizar.setFotoPerfil(newFileName);
    
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
    
                    try (InputStream inputStream = foto.getInputStream()) {
                        Path filePath = uploadPath.resolve(newFileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
    
            usuarioService.atualizarPerfil(usuarioParaAtualizar, usuarioDoForm.getNome(), usuarioDoForm.getEmail(), novaSenha); 
            
            UserDetails userDetailsAtualizado = usuarioService.buscarPorEmail(usuarioParaAtualizar.getEmail());

            Authentication novaAutenticacao = new UsernamePasswordAuthenticationToken(
                userDetailsAtualizado,
                null,
                userDetailsAtualizado.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(novaAutenticacao);

            redirectAttributes.addFlashAttribute("successMessage", "Perfil atualizado com sucesso!");
    
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocorreu um erro ao salvar a foto de perfil: " + e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocorreu um erro ao salvar o perfil: " + e.getMessage());
        }
        return "redirect:/perfil";
    }
}