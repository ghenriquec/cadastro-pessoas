package com.elotech.ServiceTests;

import com.elotech.Exceptions.InvalidCpfException;
import com.elotech.Exceptions.ValidationException;
import com.elotech.Model.Contato;
import com.elotech.Model.Pessoa;
import com.elotech.Repositories.PessoaRepository;
import com.elotech.Service.ContatoService;
import com.elotech.Service.PessoaService;
import com.elotech.dto.PessoaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PessoaServiceTest {

    @InjectMocks
    private PessoaService pessoaService;

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private ContatoService contatoService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAll() {
        List<Pessoa> pessoas = new ArrayList<>();
        Pessoa pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoas.add(pessoa);

        when(pessoaRepository.findAll()).thenReturn(pessoas);

        List<PessoaDTO> result = pessoaService.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testFindByIdSuccess() {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(1L);

        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        Pessoa result = pessoaService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testFindByIdNotFound() {
        when(pessoaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> pessoaService.findById(1L));
    }

    @Test
    public void testSaveInvalidFields() {
        Pessoa pessoa = new Pessoa();

        assertThrows(ValidationException.class, () -> pessoaService.save(pessoa));
    }

    @Test
    public void testSaveInvalidCPF() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Test");
        pessoa.setCpf("12345678901");
        pessoa.setData_nascimento(LocalDate.now().minusYears(10));

        Contato contato = new Contato();
        contato.setNome("Contato 1");
        contato.setTelefone("99999-9999");
        Set<Contato> contatos = new HashSet<>();
        contatos.add(contato);
        pessoa.setContatos(contatos);

        assertThrows(InvalidCpfException.class, () -> pessoaService.save(pessoa));
    }


    @Test
    public void testSaveSuccess() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Test");
        pessoa.setCpf("11144477735");
        pessoa.setData_nascimento(LocalDate.now().minusYears(10));

        Contato contato = new Contato();
        contato.setNome("Contato 1");
        contato.setTelefone("99999-9999");
        Set<Contato> contatos = new HashSet<>();
        contatos.add(contato);
        pessoa.setContatos(contatos);

        when(pessoaRepository.save(pessoa)).thenReturn(pessoa);

        Pessoa result = pessoaService.save(pessoa);
        assertNotNull(result);
        assertEquals(pessoa, result);
    }

}
