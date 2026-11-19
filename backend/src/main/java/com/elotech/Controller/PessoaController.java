package com.elotech.Controller;

import com.elotech.Service.PessoaService;
import com.elotech.dto.PessoaDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/pessoas")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public List<PessoaDTO> getAllPessoas() {
        return pessoaService.getAll();
    }

    @GetMapping("/{id}")
    public PessoaDTO getPessoaById(@PathVariable Long id) {
        return pessoaService.getById(id);
    }

    @PostMapping
    public ResponseEntity<PessoaDTO> createPessoa(@Valid @RequestBody PessoaDTO pessoaDTO) {
        return new ResponseEntity<>(pessoaService.create(pessoaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public PessoaDTO updatePessoa(@PathVariable Long id, @Valid @RequestBody PessoaDTO pessoaDTO) {
        return pessoaService.update(id, pessoaDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePessoa(@PathVariable Long id) {
        pessoaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
