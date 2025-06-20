package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.model.UsuarioRole;
import com.sistemaestoque.sistema_vendas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.net.MalformedURLException;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public String salvarUsuario(@Valid @ModelAttribute("usuario") Usuario usuarioDoForm,
                                BindingResult result,
                                @RequestParam("foto") MultipartFile foto,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", UsuarioRole.values());
            return "formUsuario";
        }
    
        try {
            Optional<Usuario> usuarioExistente = usuarioService.buscarPorEmailOptional(usuarioDoForm.getEmail());
            if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuarioDoForm.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "O e-mail informado já está em uso.");
                model.addAttribute("roles", UsuarioRole.values());
                model.addAttribute("usuario", usuarioDoForm);
                return "formUsuario";
            }
    
            Usuario usuarioParaSalvar = usuarioDoForm;
            if (usuarioDoForm.getId() != null) {
                Usuario original = usuarioService.buscarPorId(usuarioDoForm.getId());
                usuarioParaSalvar.setFotoPerfil(original.getFotoPerfil());
                
                if (usuarioDoForm.getSenha() == null || usuarioDoForm.getSenha().isEmpty()) {
                    usuarioParaSalvar.setSenha(original.getSenha());
                } else {
                    usuarioParaSalvar.setSenha(passwordEncoder.encode(usuarioDoForm.getSenha()));
                }
            } else {
                usuarioParaSalvar.setSenha(passwordEncoder.encode(usuarioDoForm.getSenha()));
            }
    
            if (foto != null && !foto.isEmpty()) {
                String originalFileName = foto.getOriginalFilename();
                if (originalFileName != null && !originalFileName.trim().isEmpty()) {
                    if (usuarioParaSalvar.getId() == null) {
                        usuarioService.salvar(usuarioParaSalvar);
                    }
    
                    String cleanedFileName = StringUtils.cleanPath(originalFileName);
                    String fileExtension = Optional.of(cleanedFileName)
                                                  .filter(f -> f.contains("."))
                                                  .map(f -> f.substring(f.lastIndexOf(".")))
                                                  .orElse("");
                    
                    String newFileName = "usuario_" + usuarioParaSalvar.getId() + fileExtension;
                    usuarioParaSalvar.setFotoPerfil(newFileName);
    
                    String uploadDir = "src/main/resources/static/images/perfil/";
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    try (InputStream inputStream = foto.getInputStream()) {
                        Path filePath = uploadPath.resolve(newFileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            
            usuarioService.salvar(usuarioParaSalvar);
            redirectAttributes.addFlashAttribute("successMessage", "Usuário salvo com sucesso!");
    
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao salvar o usuário: " + e.getMessage());
            model.addAttribute("roles", UsuarioRole.values());
            model.addAttribute("usuario", usuarioDoForm);
            return "formUsuario";
        }
        
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<Resource> getFotoPerfil(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            if (usuario.getFotoPerfil() == null || usuario.getFotoPerfil().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
    
            Path file = Paths.get("src/main/resources/static/images/perfil/").resolve(usuario.getFotoPerfil());
            Resource resource = new UrlResource(file.toUri());
    
            if (resource.exists() || resource.isReadable()) {
                String contentType = "application/octet-stream";
                String filename = resource.getFilename();
                if (filename != null) {
                    if (filename.toLowerCase().endsWith(".png")) {
                        contentType = MediaType.IMAGE_PNG_VALUE;
                    } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
                        contentType = MediaType.IMAGE_JPEG_VALUE;
                    }
                }
    
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .cacheControl(CacheControl.noCache().noStore().mustRevalidate())
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}