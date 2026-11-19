package com.elotech.Service;

import com.elotech.Exceptions.ConflictException;
import com.elotech.Exceptions.InvalidCpfException;
import com.elotech.Exceptions.ValidationException;
import com.elotech.Model.Contato;
import com.elotech.Model.Pessoa;
import com.elotech.Repositories.PessoaRepository;
import com.elotech.Validator.Documentos;
import com.elotech.dto.ContatoDTO;
import com.elotech.dto.PessoaDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final ContatoService contatoService;

    @Transactional(readOnly = true)
    public List<PessoaDTO> getAll() {
        return pessoaRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<Pessoa> findAll() {
        return pessoaRepository.findAll();
    }

    public Pessoa findById(Long id) {
        return pessoaRepository.findById(id).orElseThrow(() -> naoEncontrada(id));
    }

    @Transactional(readOnly = true)
    public PessoaDTO getById(Long id) {
        return convertToDTO(findById(id));
    }

    @Transactional
    public PessoaDTO create(PessoaDTO dto) {
        Pessoa pessoa = convertToEntity(dto);
        pessoa.setId(null);
        pessoa.getContatos().forEach(contato -> contato.setId(null));
        if (pessoa.getCpf() != null && pessoaRepository.existsByCpf(normalizarCpf(pessoa.getCpf()))) {
            throw cpfEmUso();
        }
        return convertToDTO(save(pessoa));
    }

    /**
     * Replaces the person's data and contact list: contacts with a known id are
     * updated, contacts without an id are added and missing ones are removed.
     */
    @Transactional
    public PessoaDTO update(Long id, PessoaDTO dto) {
        Pessoa pessoa = findById(id);
        // Query before touching the managed entity: Hibernate auto-flushes
        // pending changes before running a query.
        if (dto.getCpf() != null && pessoaRepository.existsByCpfAndIdNot(normalizarCpf(dto.getCpf()), id)) {
            throw cpfEmUso();
        }

        pessoa.setNome(trim(dto.getNome()));
        pessoa.setCpf(dto.getCpf());
        pessoa.setData_nascimento(dto.getData_nascimento());

        Map<Long, Contato> atuais = new HashMap<>();
        pessoa.getContatos().forEach(contato -> atuais.put(contato.getId(), contato));

        Set<Contato> novos = new HashSet<>();
        for (ContatoDTO contatoDTO : dto.getContatos()) {
            Contato contato = contatoDTO.getId() != null ? atuais.get(contatoDTO.getId()) : null;
            if (contato == null) {
                contato = contatoService.convertToEntity(contatoDTO);
                contato.setId(null);
            } else {
                contatoService.copyFields(contatoDTO, contato);
            }
            novos.add(contato);
        }
        pessoa.setContatos(novos);

        prepararParaSalvar(pessoa);
        // The entity is already managed: flushing cascades new contacts as
        // inserts (giving them ids) and orphan-removes the dropped ones.
        pessoaRepository.flush();
        return convertToDTO(pessoa);
    }

    public Pessoa save(Pessoa pessoa) {
        prepararParaSalvar(pessoa);
        return pessoaRepository.save(pessoa);
    }

    private void prepararParaSalvar(Pessoa pessoa) {
        validarCamposObrigatorios(pessoa);
        pessoa.setCpf(normalizarCpf(pessoa.getCpf()));
        if (!Documentos.cpfValido(pessoa.getCpf())) {
            throw new InvalidCpfException();
        }
        validarDataNascimento(pessoa);

        for (Contato contato : pessoa.getContatos()) {
            contato.setPessoa(pessoa);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        if (!pessoaRepository.existsById(id)) {
            throw naoEncontrada(id);
        }
        pessoaRepository.deleteById(id);
    }

    /** Accepts "123.456.789-09" or "12345678909" and returns only the digits. */
    static String normalizarCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
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

    private PessoaDTO convertToDTO(Pessoa pessoa) {
        PessoaDTO pessoaDTO = new PessoaDTO();
        pessoaDTO.setId(pessoa.getId());
        pessoaDTO.setNome(pessoa.getNome());
        pessoaDTO.setCpf(pessoa.getCpf());
        pessoaDTO.setData_nascimento(pessoa.getData_nascimento());
        pessoaDTO.setContatos(pessoa.getContatos().stream()
                .sorted(Comparator.comparing(Contato::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(contatoService::convertToDTO)
                .toList());
        return pessoaDTO;
    }

    public Pessoa convertToEntity(PessoaDTO pessoaDTO) {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(pessoaDTO.getId());
        pessoa.setNome(trim(pessoaDTO.getNome()));
        pessoa.setCpf(pessoaDTO.getCpf());
        pessoa.setData_nascimento(pessoaDTO.getData_nascimento());
        Set<Contato> contatos = new HashSet<>();
        if (pessoaDTO.getContatos() != null) {
            pessoaDTO.getContatos().forEach(dto -> contatos.add(contatoService.convertToEntity(dto)));
        }
        pessoa.setContatos(contatos);
        return pessoa;
    }

    private static EntityNotFoundException naoEncontrada(Long id) {
        return new EntityNotFoundException("Pessoa com ID " + id + " não encontrada.");
    }

    private static ConflictException cpfEmUso() {
        return new ConflictException("Já existe uma pessoa cadastrada com este CPF.");
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
