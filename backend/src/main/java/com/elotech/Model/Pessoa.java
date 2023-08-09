package com.elotech.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pessoas")
@Data
@NoArgsConstructor
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Nome é obrigatório.")
    @Column(length = 100)
    private String nome;

    @NotEmpty(message = "CPF é obrigatório.")
    @CPF(message = "CPF inválido.")
    @Column(unique = true)
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatória.")
    @Past(message = "Data de nascimento não pode ser uma data futura.")
    private LocalDate data_nascimento;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pessoas")
    private Set<Contato> contatos = new HashSet<>();

    public Set<Contato> getContatos() {
        return new HashSet<>(contatos);
    }

    public Pessoa(String nome, String cpf, LocalDate data_nascimento, Set<Contato> contatos) {
        this.nome = nome;
        this.cpf = cpf;
        this.data_nascimento = data_nascimento;
        this.contatos = contatos != null ? new HashSet<>(contatos) : new HashSet<>();
    }
}
