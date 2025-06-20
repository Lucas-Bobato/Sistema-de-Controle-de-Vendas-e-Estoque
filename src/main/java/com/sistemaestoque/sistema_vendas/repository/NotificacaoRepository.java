package com.sistemaestoque.sistema_vendas.repository;

import com.sistemaestoque.sistema_vendas.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuarioIdAndLidaIsFalseOrderByDataCriacaoDesc(Long usuarioId);

    @Modifying
    @Query("UPDATE Notificacao n SET n.lida = true WHERE n.usuario.id = :usuarioId")
    void marcarTodasComoLidas(@Param("usuarioId") Long usuarioId);
}