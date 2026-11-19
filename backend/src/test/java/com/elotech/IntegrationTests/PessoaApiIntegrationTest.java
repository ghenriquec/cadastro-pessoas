package com.elotech.IntegrationTests;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PessoaApiIntegrationTest extends PostgresIntegrationTest {

    private static final String PESSOA_COM_DOIS_CONTATOS = json("""
            {
              'nome': 'Maria Oliveira',
              'cpf': '529.982.247-25',
              'data_nascimento': '1985-02-15',
              'contatos': [
                {'nome': 'Trabalho', 'telefone': '+55 11 91234-5678', 'email': 'maria@empresa.com'},
                {'nome': 'Casa', 'telefone': '+55 11 3333-4444', 'email': 'maria@casa.com'}
              ]
            }
            """);

    private long criarPessoa(String body) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }

    @Test
    void criaPessoaComCpfMascaradoEVariosContatos() throws Exception {
        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PESSOA_COM_DOIS_CONTATOS))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Maria Oliveira"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.data_nascimento").value("1985-02-15"))
                .andExpect(jsonPath("$.contatos", hasSize(2)))
                .andExpect(jsonPath("$.contatos[*].id", everyItem(notNullValue())))
                .andExpect(jsonPath("$.contatos[*].email", containsInAnyOrder("maria@empresa.com", "maria@casa.com")));

        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM contatos", Integer.class)).isEqualTo(2);
    }

    @Test
    void buscaPessoaPorIdComContatosSemRecursao() throws Exception {
        long id = criarPessoa(PESSOA_COM_DOIS_CONTATOS);

        mockMvc.perform(get("/api/pessoas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.contatos", hasSize(2)))
                .andExpect(jsonPath("$.contatos[0].pessoaId").value(id))
                .andExpect(jsonPath("$.contatos[0].pessoa").doesNotExist());
    }

    @Test
    void listaPessoas() throws Exception {
        criarPessoa(PESSOA_COM_DOIS_CONTATOS);

        mockMvc.perform(get("/api/pessoas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contatos", hasSize(2)));
    }

    @Test
    void rejeitaCamposInvalidosComListaDeErros() throws Exception {
        String body = json("""
                {
                  'nome': ' ',
                  'cpf': '529.982.247-25',
                  'data_nascimento': '2999-01-01',
                  'contatos': [{'nome': 'X', 'telefone': '123', 'email': 'nao-e-email'}]
                }
                """);

        mockMvc.perform(post("/api/pessoas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Falha na validação."))
                .andExpect(jsonPath("$.erros", hasItems(
                        "Nome é obrigatório.",
                        "Data de nascimento não pode ser uma data futura.",
                        "E-mail inválido.",
                        "Telefone deve ter entre 8 e 30 caracteres.")));

        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM pessoas", Integer.class)).isZero();
    }

    @Test
    void rejeitaCpfInvalido() throws Exception {
        String body = json("{'nome': 'Fulano', 'cpf': '123.456.789-00', 'data_nascimento': '1990-01-01'}");

        mockMvc.perform(post("/api/pessoas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("CPF fornecido é inválido."));
    }

    @Test
    void rejeitaCpfDuplicadoMesmoComOutraFormatacao() throws Exception {
        criarPessoa(json("{'nome': 'Primeira', 'cpf': '52998224725', 'data_nascimento': '1990-01-01'}"));

        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'nome': 'Segunda', 'cpf': '529.982.247-25', 'data_nascimento': '1991-01-01'}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Já existe uma pessoa cadastrada com este CPF."));
    }

    @Test
    void rejeitaEmailDeContatoJaUsadoPorOutraPessoa() throws Exception {
        criarPessoa(PESSOA_COM_DOIS_CONTATOS);
        String body = json("""
                {
                  'nome': 'Outra Pessoa',
                  'cpf': '111.444.777-35',
                  'data_nascimento': '1990-01-01',
                  'contatos': [{'nome': 'Casa', 'telefone': '+55 11 99999-0000', 'email': 'maria@casa.com'}]
                }
                """);

        mockMvc.perform(post("/api/pessoas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());

        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM pessoas", Integer.class)).isEqualTo(1);
    }

    @Test
    void atualizaDadosEContatosAtualizandoAdicionandoERemovendo() throws Exception {
        long id = criarPessoa(PESSOA_COM_DOIS_CONTATOS);
        MvcResult atual = mockMvc.perform(get("/api/pessoas/{id}", id)).andReturn();
        String json = atual.getResponse().getContentAsString();
        Number trabalhoId = JsonPath.<java.util.List<Number>>read(json, "$.contatos[?(@.nome == 'Trabalho')].id").get(0);

        String body = json("""
                {
                  'nome': 'Maria Oliveira Souza',
                  'cpf': '529.982.247-25',
                  'data_nascimento': '1985-02-16',
                  'contatos': [
                    {'id': %d, 'nome': 'Trabalho', 'telefone': '+55 11 90000-0000', 'email': 'maria@nova.com'},
                    {'nome': 'Celular', 'telefone': '+55 11 98888-7777', 'email': 'maria@celular.com'}
                  ]
                }
                """.formatted(trabalhoId.longValue()));

        mockMvc.perform(put("/api/pessoas/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Oliveira Souza"))
                .andExpect(jsonPath("$.data_nascimento").value("1985-02-16"))
                .andExpect(jsonPath("$.contatos", hasSize(2)))
                .andExpect(jsonPath("$.contatos[*].id", everyItem(notNullValue())))
                .andExpect(jsonPath("$.contatos[?(@.nome == 'Trabalho')].id", contains(trabalhoId.intValue())))
                .andExpect(jsonPath("$.contatos[?(@.nome == 'Trabalho')].email", contains("maria@nova.com")))
                .andExpect(jsonPath("$.contatos[*].nome", containsInAnyOrder("Trabalho", "Celular")));

        assertThat(jdbcTemplate.queryForList("SELECT email FROM contatos ORDER BY email", String.class))
                .containsExactly("maria@celular.com", "maria@nova.com");
    }

    @Test
    void atualizarParaCpfDeOutraPessoaRetorna409SemAlterarNada() throws Exception {
        criarPessoa(json("{'nome': 'Dona do CPF', 'cpf': '111.444.777-35', 'data_nascimento': '1990-01-01'}"));
        long id = criarPessoa(json("{'nome': 'Outra', 'cpf': '529.982.247-25', 'data_nascimento': '1990-01-01'}"));

        mockMvc.perform(put("/api/pessoas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'nome': 'Outra', 'cpf': '111.444.777-35', 'data_nascimento': '1990-01-01'}")))
                .andExpect(status().isConflict());

        assertThat(jdbcTemplate.queryForObject("SELECT cpf FROM pessoas WHERE id = ?", String.class, id))
                .isEqualTo("52998224725");
    }

    @Test
    void atualizarPessoaInexistenteRetorna404() throws Exception {
        mockMvc.perform(put("/api/pessoas/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'nome': 'X', 'cpf': '52998224725', 'data_nascimento': '1990-01-01'}")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Pessoa com ID 999 não encontrada."));

        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM pessoas", Integer.class)).isZero();
    }

    @Test
    void excluiPessoaEContatosEmCascata() throws Exception {
        long id = criarPessoa(PESSOA_COM_DOIS_CONTATOS);

        mockMvc.perform(delete("/api/pessoas/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/pessoas/{id}", id)).andExpect(status().isNotFound());

        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM contatos", Integer.class)).isZero();
    }

    @Test
    void excluirPessoaInexistenteRetorna404() throws Exception {
        mockMvc.perform(delete("/api/pessoas/{id}", 999)).andExpect(status().isNotFound());
    }

    @Test
    void rejeitaJsonMalformadoEDataInvalida() throws Exception {
        mockMvc.perform(post("/api/pessoas").contentType(MediaType.APPLICATION_JSON).content("{nome:"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'nome': 'X', 'cpf': '52998224725', 'data_nascimento': '15/02/1985'}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("AAAA-MM-DD")));
    }

    @Test
    void rejeitaIdQueNaoENumero() throws Exception {
        mockMvc.perform(get("/api/pessoas/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("id deve ser do tipo Long"));
    }

    @Test
    void metodoNaoSuportadoRetorna405EmVezDe500() throws Exception {
        mockMvc.perform(patch("/api/pessoas/1")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void permiteCorsParaOrigemConfigurada() throws Exception {
        mockMvc.perform(options("/api/pessoas")
                        .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000"));

        mockMvc.perform(options("/api/pessoas")
                        .header(HttpHeaders.ORIGIN, "https://site-estranho.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden());
    }
}
