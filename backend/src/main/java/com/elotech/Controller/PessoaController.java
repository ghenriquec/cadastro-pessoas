package com.elotech.Controller;

import com.elotech.dto.PessoaDTO;
import com.elotech.Model.Pessoa;
import com.elotech.Service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public ResponseEntity<List<PessoaDTO>> getAllPessoas() {
        return new ResponseEntity<>(pessoaService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pessoa> getPessoaById(@PathVariable Long id) {
        return new ResponseEntity<>(pessoaService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Pessoa> createPessoa(@Valid @RequestBody PessoaDTO pessoaDTO) {
        Pessoa pessoa = pessoaService.convertToEntity(pessoaDTO);
        return new ResponseEntity<>(pessoaService.save(pessoa), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pessoa> updatePessoa(@PathVariable Long id, @Valid @RequestBody PessoaDTO pessoaDTO) {
        Pessoa pessoa = pessoaService.convertToEntity(pessoaDTO);
        pessoa.setId(id);
        return new ResponseEntity<>(pessoaService.save(pessoa), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePessoa(@PathVariable Long id) {
        pessoaService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
