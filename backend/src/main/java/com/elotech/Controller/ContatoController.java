package com.elotech.Controller;

import com.elotech.dto.ContatoDTO;
import com.elotech.Model.Contato;
import com.elotech.Service.ContatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/contatos")
public class ContatoController {

    @Autowired
    private ContatoService contatoService;

    @GetMapping
    public ResponseEntity<List<ContatoDTO>> getAllContatos() {
        return new ResponseEntity<>(contatoService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contato> getContatoById(@PathVariable Long id) {
        return new ResponseEntity<>(contatoService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Contato> createContato(@Valid @RequestBody ContatoDTO contatoDTO) {
        Contato contato = contatoService.convertToEntity(contatoDTO);
        return new ResponseEntity<>(contatoService.save(contato), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contato> updateContato(@PathVariable Long id, @Valid @RequestBody ContatoDTO contatoDTO) {
        Contato contato = contatoService.convertToEntity(contatoDTO);
        contato.setId(id);
        return new ResponseEntity<>(contatoService.save(contato), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContato(@PathVariable Long id) {
        contatoService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
