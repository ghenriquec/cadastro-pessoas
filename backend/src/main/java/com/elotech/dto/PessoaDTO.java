package com.elotech.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PessoaDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres.")
    private String nome;

    /** Accepted with or without the mask (123.456.789-09); returned as 11 digits. */
    @NotBlank(message = "CPF é obrigatório.")
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatória.")
    @Past(message = "Data de nascimento não pode ser uma data futura.")
    private LocalDate data_nascimento;

    private List<@Valid ContatoDTO> contatos = new ArrayList<>();

    public PessoaDTO() {
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(LocalDate data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public List<ContatoDTO> getContatos() {
        return contatos;
    }

    public void setContatos(List<ContatoDTO> contatos) {
        this.contatos = contatos != null ? contatos : new ArrayList<>();
    }
}
