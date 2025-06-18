package com.sistemaestoque.sistema_vendas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConfiguracaoController {

    @GetMapping("/configuracoes")
    public String paginaConfiguracoes() {
        return "configuracoes";
    }
}