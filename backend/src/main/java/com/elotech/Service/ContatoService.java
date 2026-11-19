package com.elotech.Service;

import com.elotech.Exceptions.ConflictException;
import com.elotech.Exceptions.ContatoNotFoundException;
import com.elotech.Exceptions.ContatoValidationException;
import com.elotech.Model.Contato;
import com.elotech.Model.Pessoa;
import com.elotech.Repositories.ContatoRepository;
import com.elotech.Repositories.PessoaRepository;
import com.elotech.dto.ContatoDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final PessoaRepository pessoaRepository;

    @Transactional(readOnly = true)
    public List<ContatoDTO> getAll() {
        return contatoRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Contato findById(Long id) {
        return contatoRepository.findById(id)
                .orElseThrow(() -> new ContatoNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public ContatoDTO getById(Long id) {
        return convertToDTO(findById(id));
    }

    @Transactional
    public ContatoDTO create(ContatoDTO dto) {
        Contato contato = convertToEntity(dto);
        contato.setId(null);
        contato.setPessoa(resolvePessoa(dto.getPessoaId()));
        if (contatoRepository.existsByEmailIgnoreCase(contato.getEmail())) {
            throw emailEmUso();
        }
        return convertToDTO(save(contato));
    }

    @Transactional
    public ContatoDTO update(Long id, ContatoDTO dto) {
        Contato contato = findById(id);
        // Run queries before changing the managed entity (Hibernate auto-flushes before queries).
        if (dto.getEmail() != null && contatoRepository.existsByEmailIgnoreCaseAndIdNot(dto.getEmail().trim(), id)) {
            throw emailEmUso();
        }
        Pessoa pessoa = dto.getPessoaId() != null ? resolvePessoa(dto.getPessoaId()) : contato.getPessoa();
        copyFields(dto, contato);
        contato.setPessoa(pessoa);
        return convertToDTO(save(contato));
    }

    public Contato save(Contato contato) {
        if (contato.getNome() == null || contato.getNome().trim().isEmpty()) {
            throw new ContatoValidationException("O nome do contato não pode estar vazio.");
        }
        return contatoRepository.save(contato);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!contatoRepository.existsById(id)) {
            throw new ContatoNotFoundException(id);
        }
        contatoRepository.deleteById(id);
    }

    public ContatoDTO convertToDTO(Contato contato) {
        ContatoDTO contatoDTO = new ContatoDTO();
        contatoDTO.setId(contato.getId());
        contatoDTO.setNome(contato.getNome());
        contatoDTO.setTelefone(contato.getTelefone());
        contatoDTO.setEmail(contato.getEmail());
        if (contato.getPessoa() != null) {
            contatoDTO.setPessoaId(contato.getPessoa().getId());
        }
        return contatoDTO;
    }

    public Contato convertToEntity(ContatoDTO contatoDTO) {
        Contato contato = new Contato();
        contato.setId(contatoDTO.getId());
        copyFields(contatoDTO, contato);
        return contato;
    }

    void copyFields(ContatoDTO from, Contato to) {
        to.setNome(trim(from.getNome()));
        to.setTelefone(trim(from.getTelefone()));
        to.setEmail(trim(from.getEmail()));
    }

    private Pessoa resolvePessoa(Long pessoaId) {
        if (pessoaId == null) {
            throw new ContatoValidationException("Informe a pessoa dona do contato (pessoaId).");
        }
        return pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa com ID " + pessoaId + " não encontrada."));
    }

    private static ConflictException emailEmUso() {
        return new ConflictException("Já existe um contato com este e-mail.");
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
