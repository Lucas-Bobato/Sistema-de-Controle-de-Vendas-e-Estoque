package com.sistemaestoque.sistema_vendas.controller;

import com.sistemaestoque.sistema_vendas.model.Notificacao;
import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<Notificacao>> getNotificacoes(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        List<Notificacao> notificacoes = notificacaoService.getNotificacoesNaoLidas(usuario.getId());
        return ResponseEntity.ok(notificacoes);
    }

    @PostMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        notificacaoService.marcarComoLida(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/marcar-todas-lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        notificacaoService.marcarTodasComoLidas(usuario.getId());
        return ResponseEntity.ok().build();
    }
}