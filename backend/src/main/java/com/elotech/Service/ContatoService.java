package com.elotech.Service;

import com.elotech.dto.ContatoDTO;
import com.elotech.Exceptions.ContatoNotFoundException;
import com.elotech.Exceptions.ContatoValidationException;
import com.elotech.Model.Contato;
import com.elotech.Repositories.ContatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContatoService {

    private final ContatoRepository contatoRepository;

    public List<ContatoDTO> getAll() {
        return contatoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Contato findById(Long id) {
        return contatoRepository.findById(id)
                .orElseThrow(() -> new ContatoNotFoundException(id));
    }

    public Contato save(Contato contato) {
        if (contato.getNome() == null || contato.getNome().trim().isEmpty()) {
            throw new ContatoValidationException("O nome do contato não pode estar vazio.");
        }
        return contatoRepository.save(contato);
    }

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
        return contatoDTO;
    }

    public Contato convertToEntity(ContatoDTO contatoDTO) {
        Contato contato = new Contato();
        contato.setId(contatoDTO.getId());
        contato.setNome(contatoDTO.getNome());
        contato.setTelefone(contatoDTO.getTelefone());
        contato.setEmail(contatoDTO.getEmail());
        return contato;
    }
}
