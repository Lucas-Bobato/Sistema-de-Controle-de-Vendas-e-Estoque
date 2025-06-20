package com.sistemaestoque.sistema_vendas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    private String mensagem;

    private LocalDateTime dataCriacao;

    private boolean lida = false;

    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    public enum TipoNotificacao {
        ESTOQUE_CRITICO,
        NOVA_VENDA,
        PERFIL_ATUALIZADO
    }
}