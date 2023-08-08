package com.elotech.ModelTests;

import com.elotech.Model.Contato;
import com.elotech.Model.Pessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PessoaTest {

    private Pessoa pessoa;

    @BeforeEach
    void setUp() {
        pessoa = new Pessoa();
    }

    @Test
    void testId() {
        Long id = 1L;
        pessoa.setId(id);
        assertEquals(id, pessoa.getId());
    }

    @Test
    void testNome() {
        String nome = "Guilherme";
        pessoa.setNome(nome);
        assertEquals(nome, pessoa.getNome());
    }

    @Test
    void testCpf() {
        String cpf = "123.456.789-00";
        pessoa.setCpf(cpf);
        assertEquals(cpf, pessoa.getCpf());
    }

    @Test
    void testDataNascimento() {
        LocalDate date = LocalDate.of(2004, 5, 5);
        pessoa.setDataNascimento(date);
        assertEquals(date, pessoa.getDataNascimento());
    }

    @Test
    void testContatos() {
        Set<Contato> contatos = new HashSet<>();
        Contato contato = new Contato();
        contatos.add(contato);
        pessoa.setContatos(contatos);
        assertNotNull(pessoa.getContatos());
        assertFalse(pessoa.getContatos().isEmpty());
    }

    @Test
    void testConstructor() {
        Set<Contato> contatos = new HashSet<>();
        Contato contato = new Contato();
        contatos.add(contato);

        Pessoa novaPessoa = new Pessoa("Guilherme", "123.456.789-00", LocalDate.of(2004, 5, 5), contatos);
        assertEquals("Guilherme", novaPessoa.getNome());
        assertEquals("123.456.789-00", novaPessoa.getCpf());
        assertEquals(LocalDate.of(2004, 5, 5), novaPessoa.getDataNascimento());
        assertFalse(novaPessoa.getContatos().isEmpty());
    }
}
