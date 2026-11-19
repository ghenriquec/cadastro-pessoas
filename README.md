# People Registration — Java & React

**A full-stack CRUD project for people and their contacts.**

This repository contains a Java/Spring Boot backend and a React frontend. It is a learning/technical-assessment project, not a production-ready identity or personal-data platform.

## Tech stack

| Component | Technologies |
| --- | --- |
| Backend | Java 17, Spring Boot 3.1.2, Spring Data JPA, Jakarta Bean Validation, Flyway |
| Database | PostgreSQL |
| Frontend | React 18, React Router, Material UI, Axios |
| Testing | JUnit 5, Mockito, Spring MockMvc, Testcontainers (PostgreSQL 16) |

## Repository structure

```text
cadastro-pessoas/
├── backend/                         # Spring Boot / Maven application
│   ├── pom.xml
│   └── src/
│       ├── main/resources/db/migration/   # Flyway migrations
│       └── test/                          # unit + integration tests
├── cadastro-frontend/               # React client
│   ├── package.json
│   └── src/
└── .github/workflows/               # CI and manual Azure deployment
```

## Run locally

Prerequisites: Java 17, Maven (or the Maven wrapper), Node.js/npm, and a PostgreSQL instance.

1. Create a PostgreSQL database named `apicadastro`.
2. Set `DB_USERNAME` and `DB_PASSWORD` to credentials for your local database. The backend connects to `jdbc:postgresql://localhost:5432/apicadastro` unless `DB_URL` is set.
3. Start the backend. Flyway creates or updates the schema on startup:

   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

4. In another terminal, start the frontend:

   ```bash
   cd cadastro-frontend
   npm ci
   npm start
   ```

5. Open [http://localhost:3000](http://localhost:3000).

Optional local settings can be stored in the ignored `backend/src/main/resources/application-private.properties` file. Do not commit real database credentials.

### Configuration

| Variable | Where | Default | Purpose |
| --- | --- | --- | --- |
| `DB_URL` | backend | `jdbc:postgresql://localhost:5432/apicadastro` | JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | backend | — | Database credentials |
| `CORS_ALLOWED_ORIGINS` | backend | `http://localhost:3000` | Comma-separated frontend origins allowed to call the API |
| `REACT_APP_API_URL` | frontend (build time) | `http://localhost:8080/api` | Backend API base URL (see `cadastro-frontend/.env.example`) |

## Database migrations

The schema is versioned with Flyway in [`backend/src/main/resources/db/migration`](backend/src/main/resources/db/migration). Hibernate runs with `ddl-auto=validate`, so the application refuses to start if the entities and the schema drift apart.

Databases created by the old `ddl-auto=update` setup are baselined at version 1 automatically (`spring.flyway.baseline-on-migrate=true`); new migrations apply on top. To change the schema, add a new `V2__description.sql` file. Never edit a migration that has already run.

## API overview

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/pessoas` | List people with their contacts |
| GET | `/api/pessoas/{id}` | Find one person |
| POST | `/api/pessoas` | Create a person, optionally with contacts |
| PUT | `/api/pessoas/{id}` | Replace a person's data and contact list |
| DELETE | `/api/pessoas/{id}` | Delete a person and their contacts |
| GET | `/api/contatos`, `/api/contatos/{id}` | List or find contacts |
| POST | `/api/contatos` | Add a contact to a person (`pessoaId` required) |
| PUT / DELETE | `/api/contatos/{id}` | Update or delete a contact |

Example person payload:

```json
{
  "nome": "Maria Oliveira",
  "cpf": "529.982.247-25",
  "data_nascimento": "1985-02-15",
  "contatos": [
    { "nome": "Trabalho", "telefone": "+55 11 91234-5678", "email": "maria@empresa.com" }
  ]
}
```

- **CPF**: accepted with or without the mask, stored and returned as 11 digits, and must be unique.
- **Updating contacts with `PUT /api/pessoas/{id}`**: contacts with an `id` are updated, contacts without one are created, and contacts left out of the list are deleted.
- **Contact e-mails** are unique.

### Error contract

Every error returns the same shape:

```json
{ "status": 400, "mensagem": "Falha na validação.", "erros": ["Nome é obrigatório."] }
```

| Status | When |
| --- | --- |
| 400 | Invalid fields (with `erros`), invalid CPF, malformed JSON or date, non-numeric id |
| 404 | Person or contact not found (get, update, delete) |
| 409 | CPF or contact e-mail already registered |
| 405 | HTTP method not supported |

## Testing

```bash
mvn --batch-mode -f backend/pom.xml verify
```

- **Unit tests** (JUnit + Mockito): models, services and the exception handler.
- **Integration tests** (`backend/src/test/java/com/elotech/IntegrationTests`): start the whole application against a real PostgreSQL in a container (Testcontainers), run the Flyway migrations, and exercise the HTTP API through MockMvc. They cover validation and error responses, CPF normalization and uniqueness, contact create/update/remove, cascade delete, CORS, and the migrated schema.

The integration tests need Docker running. The [Java tests workflow](.github/workflows/java-tests.yml) runs everything on each change to `backend/`.

## Deployment

The [Azure deployment workflow](.github/workflows/main_elotech.yml) builds `backend/` and deploys the JAR to the `elotech` Azure Web App. It runs **manually only** (Actions → "Build and deploy JAR app to Azure Web App" → Run workflow). Before running it, make sure the Web App exists, the publish-profile secret is valid, and `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` and `CORS_ALLOWED_ORIGINS` are set in the App Service configuration.

For the frontend on Vercel, set `REACT_APP_API_URL` to the deployed backend URL (ending in `/api`).

## Known limitations

- No authentication or authorization: anyone who can reach the API can read and change data, including CPFs. Do not use with real personal data.
- No pagination on the list endpoints.
- The frontend does not show the API's validation messages to the user yet.

## Author

[Guilherme Henrique](https://github.com/ghenriquec) · [LinkedIn](https://www.linkedin.com/in/guihenriquecoelho/)
