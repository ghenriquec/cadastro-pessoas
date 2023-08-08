package com.elotech.Exceptions;

public class InvalidCpfException extends RuntimeException {

    public InvalidCpfException() {
        super("CPF fornecido é inválido.");
    }

    public InvalidCpfException(String message) {
        super(message);
    }
}
