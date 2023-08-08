package com.elotech.ServiceTests;

import com.elotech.Exceptions.ContatoNotFoundException;
import com.elotech.Exceptions.ContatoValidationException;
import com.elotech.Model.Contato;
import com.elotech.Repositories.ContatoRepository;
import com.elotech.Service.ContatoService;
import com.elotech.dto.ContatoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContatoServiceTest {

    @InjectMocks
    private ContatoService contatoService;

    @Mock
    private ContatoRepository contatoRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByIdSuccess() {
        Contato contato = new Contato();
        contato.setId(1L);
        contato.setNome("Test Name");

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));

        Contato result = contatoService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testFindByIdNotFound() {
        when(contatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ContatoNotFoundException.class, () -> contatoService.findById(1L));
    }

    @Test
    public void testSaveWithEmptyName() {
        Contato contato = new Contato();
        contato.setNome("");

        assertThrows(ContatoValidationException.class, () -> contatoService.save(contato));
    }

    @Test
    public void testSaveSuccess() {
        Contato contato = new Contato();
        contato.setNome("Test Name");

        when(contatoRepository.save(any(Contato.class))).thenReturn(contato);

        Contato savedContato = contatoService.save(contato);

        assertNotNull(savedContato);
        assertEquals("Test Name", savedContato.getNome());
    }
}
