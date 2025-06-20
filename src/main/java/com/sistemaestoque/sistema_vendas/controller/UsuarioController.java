package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.model.UsuarioRole;
import com.sistemaestoque.sistema_vendas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model, @RequestParam(defaultValue = "nome") String sortField, @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        model.addAttribute("usuarios", usuarioService.listarTodos(sort));
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "Usuarios";
    }

    @GetMapping("/novo")
    public String novoUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", UsuarioRole.values());
        return "formUsuario";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        model.addAttribute("roles", UsuarioRole.values());
        return "formUsuario";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                BindingResult result,
                                @RequestParam("foto") MultipartFile foto,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", UsuarioRole.values());
            return "formUsuario";
        }
    
        try {
            String senhaPlana = usuario.getSenha();
            if (usuario.getId() == null) {
                usuario.setSenha("placeholder");
            }
    
            usuarioService.salvar(usuario);
    
            if(senhaPlana != null && !senhaPlana.isEmpty()){
                usuario.setSenha(senhaPlana);
                usuarioService.salvar(usuario);
            }
    
            if (foto != null && !foto.isEmpty()) {
                String originalFileName = foto.getOriginalFilename();
                if (originalFileName != null && !originalFileName.trim().isEmpty()) {
                    String cleanedFileName = StringUtils.cleanPath(originalFileName);
                    String fileExtension = Optional.of(cleanedFileName)
                                                  .filter(f -> f.contains("."))
                                                  .map(f -> f.substring(f.lastIndexOf(".")))
                                                  .orElse("");
                    
                    String newFileName = "usuario_" + usuario.getId() + fileExtension;
                    usuario.setFotoPerfil(newFileName);

                    String uploadDir = "src/main/resources/static/images/perfil/";
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    try (InputStream inputStream = foto.getInputStream()) {
                        Path filePath = uploadPath.resolve(newFileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    usuarioService.salvar(usuario);
                }
            }
            redirectAttributes.addFlashAttribute("successMessage", "Usuário salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", UsuarioRole.values());
            model.addAttribute("usuario", usuario);
            return "formUsuario";
        }
        
        return "redirect:/usuarios";
    }
}