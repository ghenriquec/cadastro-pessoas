package com.elotech.Exceptions;

import com.elotech.Response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void invalidCpfReturnsBadRequest() {
        ResponseEntity<ErrorResponse> response =
                handler.handleDomainValidation(new InvalidCpfException());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CPF fornecido é inválido.", response.getBody().getMensagem());
    }

    @Test
    void missingRequiredFieldReturnsBadRequest() {
        ResponseEntity<ErrorResponse> response =
                handler.handleDomainValidation(new ValidationException("Nome é obrigatório."));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void invalidContactReturnsBadRequest() {
        ResponseEntity<ErrorResponse> response =
                handler.handleDomainValidation(new ContatoValidationException("Contato inválido."));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Contato inválido.", response.getBody().getMensagem());
    }

    @Test
    void missingContactReturnsNotFound() {
        ResponseEntity<ErrorResponse> response =
                handler.handleContatoNotFound(new ContatoNotFoundException(42L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
    }
}
