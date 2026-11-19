package com.elotech.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContatoDTO {

    private Long id;

    @NotBlank(message = "Nome do contato é obrigatório.")
    @Size(max = 100, message = "Nome do contato deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "Telefone é obrigatório.")
    @Size(min = 8, max = 30, message = "Telefone deve ter entre 8 e 30 caracteres.")
    private String telefone;

    @NotBlank(message = "E-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    /** Owner of the contact. Required on /api/contatos; ignored inside a person payload. */
    private Long pessoaId;

    public ContatoDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(Long pessoaId) {
        this.pessoaId = pessoaId;
    }
}
