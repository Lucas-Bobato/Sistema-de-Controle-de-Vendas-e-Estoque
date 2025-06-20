package com.sistemaestoque.sistema_vendas.service;

import com.sistemaestoque.sistema_vendas.model.Notificacao;
import com.sistemaestoque.sistema_vendas.model.Usuario;
import com.sistemaestoque.sistema_vendas.repository.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    public void criarNotificacao(Usuario usuario, String mensagem, Notificacao.TipoNotificacao tipo) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setDataCriacao(LocalDateTime.now());
        notificacao.setLida(false);
        notificacaoRepository.save(notificacao);
    }

    public List<Notificacao> getNotificacoesNaoLidas(Long usuarioId) {
        return notificacaoRepository.findByUsuarioIdAndLidaIsFalseOrderByDataCriacaoDesc(usuarioId);
    }
}