package com.elotech.ModelTests;

import com.elotech.Model.Contato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContatoTest {

    private Contato contato;

    @BeforeEach
    public void setUp() {
        contato = new Contato();
    }

    @Test
    public void testSetName() {
        String nome = "Guilherme";
        contato.setNome(nome);
        assertEquals(nome, contato.getNome());
    }

    @Test
    public void testSetTelefone() {
        String telefone = "99999-9999";
        contato.setTelefone(telefone);
        assertEquals(telefone, contato.getTelefone());
    }

    @Test
    public void testSetEmail() {
        String email = "Guilherme@example.com";
        contato.setEmail(email);
        assertEquals(email, contato.getEmail());
    }
    @Test
    public void testToString() {
        String nome = "Guilherme";
        String telefone = "99999-9999";
        String email = "Guilherme@example.com";
        contato.setNome(nome);
        contato.setTelefone(telefone);
        contato.setEmail(email);
        assertTrue(contato.toString().contains(nome));
        assertTrue(contato.toString().contains(telefone));
        assertTrue(contato.toString().contains(email));
    }
}
