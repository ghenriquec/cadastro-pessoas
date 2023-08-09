package com.elotech.Service;

import com.elotech.Validator.Documentos;
import com.elotech.dto.PessoaDTO;
import com.elotech.Exceptions.InvalidCpfException;
import com.elotech.Exceptions.ValidationException;
import com.elotech.Model.Contato;
import com.elotech.Model.Pessoa;
import com.elotech.Repositories.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final ContatoService contatoService;

    public List<PessoaDTO> getAll() {
        return pessoaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Pessoa> findAll() {
        return pessoaRepository.findAll();
    }

    public Pessoa findById(Long id) {
        return pessoaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pessoa com ID " + id + " não encontrada."));
    }

    public Pessoa save(Pessoa pessoa) {
        validarCamposObrigatorios(pessoa);
        validarDataNascimento(pessoa);

        for (Contato contato : pessoa.getContatos()) {
            contato.setPessoas(pessoa);
        }

        Pessoa savedPessoa = pessoaRepository.save(pessoa); 
        return savedPessoa;
    }

    private void validarCamposObrigatorios(Pessoa pessoa) {
        if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty()) {
            throw new ValidationException("Nome é obrigatório.");
        }

        if (pessoa.getCpf() == null || pessoa.getCpf().trim().isEmpty()) {
            throw new ValidationException("CPF é obrigatório.");
        }

        if (pessoa.getData_nascimento() == null) {
            throw new ValidationException("Data de nascimento é obrigatória.");
        }
    }

    private void validarDataNascimento(Pessoa pessoa) {
        if (pessoa.getData_nascimento().isAfter(LocalDate.now())) {
            throw new ValidationException("A data de nascimento não pode ser uma data futura.");
        }
    }

    public void deleteById(Long id) {
        pessoaRepository.deleteById(id);
    }

    private PessoaDTO convertToDTO(Pessoa pessoa) {
        PessoaDTO pessoaDTO = new PessoaDTO();
        pessoaDTO.setId(pessoa.getId());
        pessoaDTO.setNome(pessoa.getNome());
        pessoaDTO.setCpf(pessoa.getCpf());
        pessoaDTO.setData_nascimento(pessoa.getData_nascimento());
        pessoaDTO.setContatos(pessoa.getContatos().stream()
                .map(contato -> contatoService.convertToDTO(contato))
                .collect(Collectors.toList()));
        return pessoaDTO;
    }

    public Pessoa convertToEntity(PessoaDTO pessoaDTO) {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(pessoaDTO.getId());
        pessoa.setNome(pessoaDTO.getNome());
        pessoa.setCpf(pessoaDTO.getCpf());
        pessoa.setData_nascimento(pessoaDTO.getData_nascimento());
        return pessoa;
    }
}
