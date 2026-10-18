# People Registration — Java & React

**A full-stack CRUD project for people and their contacts.**

This repository contains a Java/Spring Boot backend and a React frontend. It is a learning/technical-assessment project, not a production-ready identity or personal-data platform.

## Tech stack

| Component | Technologies |
| --- | --- |
| Backend | Java 17, Spring Boot 3.1.2, Spring Data JPA, Jakarta Bean Validation |
| Database | PostgreSQL |
| Frontend | React 18, React Router, Material UI, Axios |
| Testing | JUnit 5, Mockito; backend tests under `backend/src/test/` |

## Repository structure

```text
cadastro-pessoas/
├── backend/                 # Spring Boot / Maven application
│   ├── pom.xml
│   └── src/
├── cadastro-frontend/       # React client
│   ├── package.json
│   └── src/
└── .github/workflows/       # CI and existing Azure deployment workflow
```

## Run locally

Prerequisites: Java 17, Maven (or the Maven wrapper), Node.js/npm, and a PostgreSQL instance.

1. Create a PostgreSQL database named `apicadastro`.
2. Set `DB_USERNAME` and `DB_PASSWORD` to credentials for your local database. The backend connects to `jdbc:postgresql://localhost:5432/apicadastro` by default.
3. Start the backend:

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

Optional local settings can be stored in the ignored `backend/src/main/resources/application-private.properties` file. Do not commit real database credentials. The configured `spring.jpa.hibernate.ddl-auto=update` is intended only for local development; review schema migrations before using real data.

## API overview

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/pessoas` | List people |
| GET | `/api/pessoas/{id}` | Find one person |
| POST | `/api/pessoas` | Create a person |
| PUT | `/api/pessoas/{id}` | Update a person |
| DELETE | `/api/pessoas/{id}` | Delete a person |
| GET / POST / PUT / DELETE | `/api/contatos` and `/api/contatos/{id}` | Manage contact records |

Consult [PessoaController](backend/src/main/java/com/elotech/Controller/PessoaController.java) and [ContatoController](backend/src/main/java/com/elotech/Controller/ContatoController.java) for the exact current request/response contracts.

## Testing

The backend contains JUnit/Mockito unit tests:

```bash
mvn --batch-mode -f backend/pom.xml test
```

The new [Java tests workflow](.github/workflows/java-tests.yml) runs Maven tests on relevant changes. This tests backend unit behavior, not database integration, end-to-end flows, or security.

## Known limitations and engineering opportunities

- DTO validation and error contracts need dedicated controller/API integration tests.
- Persistence behavior should be tested against PostgreSQL in an isolated test environment.
- Database schema changes should use a migration tool rather than relying on Hibernate `ddl-auto=update`.
- Authentication, authorization, and sensitive-data handling are not established as production-ready.
- The historical [Azure deployment workflow](.github/workflows/main_elotech.yml) needs review before use; it assumes a root-level Maven build, while the actual `pom.xml` lives in `backend/`. **Do not assume that workflow deploys successfully.**

## Author

[Guilherme Henrique](https://github.com/ghenriquec) · [LinkedIn](https://www.linkedin.com/in/guihenriquecoelho/)
