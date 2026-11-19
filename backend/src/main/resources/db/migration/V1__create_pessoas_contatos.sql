-- Initial schema. Databases created earlier by Hibernate ddl-auto are
-- baselined at version 1 (spring.flyway.baseline-on-migrate), so this
-- script only runs on new, empty databases.

CREATE TABLE pessoas (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    cpf             VARCHAR(11)  NOT NULL,
    data_nascimento DATE         NOT NULL,
    CONSTRAINT uk_pessoas_cpf UNIQUE (cpf)
);

CREATE TABLE contatos (
    id        BIGSERIAL PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    telefone  VARCHAR(30)  NOT NULL,
    email     VARCHAR(255) NOT NULL,
    pessoa_id BIGINT       NOT NULL,
    CONSTRAINT uk_contatos_email UNIQUE (email),
    CONSTRAINT fk_contatos_pessoa FOREIGN KEY (pessoa_id)
        REFERENCES pessoas (id) ON DELETE CASCADE
);

CREATE INDEX idx_contatos_pessoa_id ON contatos (pessoa_id);
