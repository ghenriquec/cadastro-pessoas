# People Registration

This project is a backend application developed in Java with Spring Boot that allows the registration and management of people. The application uses PostgreSQL as the database and follows a basic architecture of controller, service, and repository for code organization.

## Features

- Registration of new people
- Updating information of registered people
- Deletion of people records
- Querying people information by ID
- Listing all registered people

## Technologies Used

- Java 17
- Spring Boot 2.7.x
- Spring Data JPA
- PostgreSQL
- Lombok

## Project Structure

```plaintext
cadastro-pessoas/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ghenriquec/
│   │   │           └── cadastropessoas/
│   │   │               ├── controller/
│   │   │               │   └── PessoaController.java
│   │   │               ├── model/
│   │   │               │   └── Pessoa.java
│   │   │               ├── repository/
│   │   │               │   └── PessoaRepository.java
│   │   │               ├── service/
│   │   │               │   └── PessoaService.java
│   │   │               └── CadastropessoasApplication.java
│   │   └── resources/
│   │       └── application.properties
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Environment Setup

1. **Install dependencies**:
   - [JDK 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
   - [PostgreSQL](https://www.postgresql.org/download/)
   - [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)

2. **Configure the database**:
   - Create a PostgreSQL database named `people_registration`.
   - Update the connection properties in the `application.properties` file:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/people_registration
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     spring.jpa.show-sql=true
     ```

3. **Run the application**:
   - In IntelliJ IDEA, open the project and run the `PeopleApplication` class.

## Endpoints

The application exposes the following REST endpoints for managing people:

- **GET /api/people**: Lists all people
- **GET /api/people/{id}**: Gets a person by ID
- **POST /api/people**: Creates a new person
- **PUT /api/people/{id}**: Updates a person's information
- **DELETE /api/people/{id}**: Removes a person by ID

## Contribution

Contributions are welcome! Feel free to open issues and pull requests.

## License

This project is licensed under the [MIT License](LICENSE).
