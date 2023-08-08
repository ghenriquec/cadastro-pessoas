package com.elotech.Exceptions;

public class ContatoNotFoundException extends RuntimeException {
    public ContatoNotFoundException(Long id) {
        super("Contato com ID " + id + " não foi encontrado.");
    }
}