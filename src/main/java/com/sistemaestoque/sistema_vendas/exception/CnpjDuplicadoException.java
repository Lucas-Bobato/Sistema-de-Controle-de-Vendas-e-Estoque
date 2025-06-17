package com.sistemaestoque.sistema_vendas.exception;

public class CnpjDuplicadoException extends RuntimeException {
    public CnpjDuplicadoException(String message) {
        super(message);
    }
}