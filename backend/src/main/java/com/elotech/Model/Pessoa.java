package com.elotech.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "pessoas")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "contatos")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Nome é obrigatório.")
    @Column(nullable = false, length = 100)
    private String nome;

    /** Stored as 11 digits, without punctuation. */
    @NotEmpty(message = "CPF é obrigatório.")
    @CPF(message = "CPF inválido.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatória.")
    @Past(message = "Data de nascimento não pode ser uma data futura.")
    @Column(nullable = false)
    private LocalDate data_nascimento;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pessoa")
    private Set<Contato> contatos = new HashSet<>();

    public Pessoa(String nome, String cpf, LocalDate data_nascimento, Set<Contato> contatos) {
        this.nome = nome;
        this.cpf = cpf;
        this.data_nascimento = data_nascimento;
        setContatos(contatos);
    }

    public Set<Contato> getContatos() {
        return new HashSet<>(contatos);
    }

    public void setContatos(Set<Contato> contatos) {
        this.contatos.clear();
        if (contatos != null) {
            contatos.forEach(this::addContato);
        }
    }

    public void addContato(Contato contato) {
        contato.setPessoa(this);
        contatos.add(contato);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pessoa other)) return false;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
