package com.elotech.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Table(name = "contatos")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "pessoa")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do contato é obrigatório.")
    @Size(max = 100, message = "Nome do contato deve ter no máximo 100 caracteres.")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Telefone é obrigatório.")
    @Size(min = 8, max = 30, message = "Telefone deve ter entre 8 e 30 caracteres.")
    @Column(name = "telefone", nullable = false, length = 30)
    private String telefone;

    @NotBlank(message = "E-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    // Entities without an id are only equal to themselves, so several new
    // contacts can live in the same Set before they are saved.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contato other)) return false;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
