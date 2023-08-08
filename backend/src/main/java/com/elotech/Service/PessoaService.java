package com.elotech.Service;

import com.elotech.Validator.Documentos;
import com.elotech.dto.PessoaDTO;
import com.elotech.Exceptions.InvalidCpfException;
import com.elotech.Exceptions.ValidationException;
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
        if (pessoa.getNome() == null || pessoa.getNome().trim().isEmpty() ||
                pessoa.getCpf() == null || pessoa.getCpf().trim().isEmpty() ||
                pessoa.getDataNascimento() == null ||
                pessoa.getContatos() == null || pessoa.getContatos().isEmpty()) {
            throw new ValidationException("Todos os campos são obrigatórios e uma pessoa deve possuir ao menos um contato.");
        }

        if (pessoa.getContatos().isEmpty()) {
            throw new ValidationException("Uma pessoa deve possuir ao menos um contato.");
        }

        String cpf = pessoa.getCpf().replaceAll("[^0-9]", ""); // Remove caracteres não numéricos do CPF

        if (cpf.length() != 11) {
            throw new InvalidCpfException("CPF deve conter exatamente 11 dígitos.");
        }

        CPFValidator cpfValidator = new CPFValidator();
        if (!Documentos.cpfValido(cpf)) {
            throw new InvalidCpfException("CPF inválido.");
        }

        if (pessoa.getDataNascimento().isAfter(LocalDate.now())) {
            throw new ValidationException("A data de nascimento não pode ser uma data futura.");
        }

        return pessoaRepository.save(pessoa);
    }

    public void deleteById(Long id) {
        pessoaRepository.deleteById(id);
    }

    private PessoaDTO convertToDTO(Pessoa pessoa) {
        PessoaDTO pessoaDTO = new PessoaDTO();
        pessoaDTO.setId(pessoa.getId());
        pessoaDTO.setNome(pessoa.getNome());
        pessoaDTO.setCpf(pessoa.getCpf());
        pessoaDTO.setDataNascimento(pessoa.getDataNascimento());
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
        pessoa.setDataNascimento(pessoaDTO.getDataNascimento());
        return pessoa;
    }
}
