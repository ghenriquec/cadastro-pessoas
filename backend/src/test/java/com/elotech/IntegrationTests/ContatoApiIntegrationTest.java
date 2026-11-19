package com.elotech.IntegrationTests;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ContatoApiIntegrationTest extends PostgresIntegrationTest {

    private long pessoaId;

    @BeforeEach
    void criarPessoa() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'nome': 'Roberto Alves', 'cpf': '52998224725', 'data_nascimento': '1987-03-10'}")))
                .andExpect(status().isCreated())
                .andReturn();
        pessoaId = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }

    private String contato(String email) {
        return json("{'nome': 'Celular', 'telefone': '+55 11 92345-6789', 'email': '%s', 'pessoaId': %d}"
                .formatted(email, pessoaId));
    }

    private long criarContato(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/contatos").contentType(MediaType.APPLICATION_JSON).content(contato(email)))
                .andExpect(status().isCreated())
                .andReturn();
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }

    @Test
    void criaContatoParaPessoaExistente() throws Exception {
        mockMvc.perform(post("/api/contatos").contentType(MediaType.APPLICATION_JSON).content(contato("roberto@email.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.pessoaId").value(pessoaId))
                .andExpect(jsonPath("$.email").value("roberto@email.com"));

        mockMvc.perform(get("/api/pessoas/{id}", pessoaId))
                .andExpect(jsonPath("$.contatos", hasSize(1)));
    }

    @Test
    void exigePessoaDonaDoContato() throws Exception {
        mockMvc.perform(post("/api/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'nome': 'Celular', 'telefone': '+55 11 92345-6789', 'email': 'a@b.com'}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Informe a pessoa dona do contato (pessoaId)."));
    }

    @Test
    void pessoaInexistenteRetorna404() throws Exception {
        mockMvc.perform(post("/api/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'nome': 'Celular', 'telefone': '+55 11 92345-6789', 'email': 'a@b.com', 'pessoaId': 999}")))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejeitaContatoInvalido() throws Exception {
        mockMvc.perform(post("/api/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'nome': '', 'telefone': '', 'email': 'invalido', 'pessoaId': %d}".formatted(pessoaId))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros", hasSize(4)));
    }

    @Test
    void rejeitaEmailDuplicado() throws Exception {
        criarContato("roberto@email.com");

        mockMvc.perform(post("/api/contatos").contentType(MediaType.APPLICATION_JSON).content(contato("ROBERTO@email.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Já existe um contato com este e-mail."));
    }

    @Test
    void atualizaContato() throws Exception {
        long id = criarContato("roberto@email.com");

        mockMvc.perform(put("/api/contatos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'nome': 'Trabalho', 'telefone': '+55 11 3000-1000', 'email': 'roberto@empresa.com'}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Trabalho"))
                .andExpect(jsonPath("$.pessoaId").value(pessoaId));

        assertThat(jdbcTemplate.queryForObject("SELECT email FROM contatos WHERE id = ?", String.class, id))
                .isEqualTo("roberto@empresa.com");
    }

    @Test
    void listaEExcluiContato() throws Exception {
        long id = criarContato("roberto@email.com");

        mockMvc.perform(get("/api/contatos")).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(delete("/api/contatos/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/contatos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Contato com ID " + id + " não foi encontrado."));
        mockMvc.perform(delete("/api/contatos/{id}", id)).andExpect(status().isNotFound());
    }
}
