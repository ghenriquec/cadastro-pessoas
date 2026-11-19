package com.elotech.IntegrationTests;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayMigrationIntegrationTest extends PostgresIntegrationTest {

    @Test
    void aplicaAsMigracoesNoBancoVazio() {
        List<Map<String, Object>> historico = jdbcTemplate.queryForList(
                "SELECT version, success FROM flyway_schema_history WHERE version IS NOT NULL ORDER BY installed_rank");

        assertThat(historico).isNotEmpty();
        assertThat(historico).allSatisfy(linha -> assertThat(linha.get("success")).isEqualTo(true));
        assertThat(historico.get(0).get("version")).isEqualTo("1");
    }

    @Test
    void criaRestricoesDeUnicidadeEChaveEstrangeira() {
        List<String> constraints = jdbcTemplate.queryForList(
                "SELECT conname FROM pg_constraint WHERE conrelid IN ('pessoas'::regclass, 'contatos'::regclass)",
                String.class);

        assertThat(constraints).contains("uk_pessoas_cpf", "uk_contatos_email", "fk_contatos_pessoa");
    }
}
