package com.elotech.Response;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ErrorResponse {
    private int status;
    private String mensagem;
    private List<String> erros;

    public ErrorResponse() {}

    public ErrorResponse(HttpStatus status, String mensagem) {
        this.status = status.value();
        this.mensagem = mensagem;
    }

    public ErrorResponse(HttpStatus status, String mensagem, List<String> erros) {
        this.status = status.value();
        this.mensagem = mensagem;
        this.erros = erros;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public List<String> getErros() {
        return erros;
    }

    public void setErros(List<String> erros) {
        this.erros = erros;
    }
}
