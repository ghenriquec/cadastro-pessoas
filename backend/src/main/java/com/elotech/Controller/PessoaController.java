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

    private final PessoaService pessoaService;

    @Autowired
    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public ResponseEntity<List<PessoaDTO>> getAllPessoas() {
        List<PessoaDTO> pessoas = pessoaService.getAll();
        return new ResponseEntity<>(pessoas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pessoa> getPessoaById(@PathVariable Long id) {
        Pessoa pessoa = pessoaService.findById(id);
        return new ResponseEntity<>(pessoa, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Pessoa> createPessoa(@Valid @RequestBody PessoaDTO pessoaDTO) {
    Pessoa pessoa = pessoaService.convertToEntity(pessoaDTO);
    Pessoa savedPessoa = pessoaService.save(pessoa);
    return new ResponseEntity<>(savedPessoa, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pessoa> updatePessoa(@PathVariable Long id, @Valid @RequestBody PessoaDTO pessoaDTO) {
        Pessoa pessoa = pessoaService.convertToEntity(pessoaDTO);
        pessoa.setId(id);
        Pessoa updatedPessoa = pessoaService.save(pessoa);
        return new ResponseEntity<>(updatedPessoa, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePessoa(@PathVariable Long id) {
        pessoaService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
