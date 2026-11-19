package com.elotech.Controller;

import com.elotech.Service.ContatoService;
import com.elotech.dto.ContatoDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/contatos")
public class ContatoController {

    private final ContatoService contatoService;

    public ContatoController(ContatoService contatoService) {
        this.contatoService = contatoService;
    }

    @GetMapping
    public List<ContatoDTO> getAllContatos() {
        return contatoService.getAll();
    }

    @GetMapping("/{id}")
    public ContatoDTO getContatoById(@PathVariable Long id) {
        return contatoService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ContatoDTO> createContato(@Valid @RequestBody ContatoDTO contatoDTO) {
        return new ResponseEntity<>(contatoService.create(contatoDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ContatoDTO updateContato(@PathVariable Long id, @Valid @RequestBody ContatoDTO contatoDTO) {
        return contatoService.update(id, contatoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContato(@PathVariable Long id) {
        contatoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
