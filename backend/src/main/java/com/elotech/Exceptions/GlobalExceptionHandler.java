package com.elotech.Exceptions;

import com.elotech.Response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return construirErrorResponse(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ContatoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleContatoNotFound(ContatoNotFoundException ex) {
        return construirErrorResponse(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler({
            InvalidCpfException.class,
            ValidationException.class,
            ContatoValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleDomainValidation(RuntimeException ex) {
        return construirErrorResponse(new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return construirErrorResponse(new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return construirErrorResponse(new ErrorResponse(HttpStatus.CONFLICT,
                "Os dados conflitam com um registro existente (CPF ou e-mail já cadastrado)."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> erros = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> erros.add(error.getDefaultMessage()));
        return construirErrorResponse(new ErrorResponse(HttpStatus.BAD_REQUEST, "Falha na validação.", erros));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> erros = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .toList();
        return construirErrorResponse(new ErrorResponse(HttpStatus.BAD_REQUEST, "Falha na validação.", erros));
    }

    /** Bean Validation failures raised while Hibernate flushes arrive wrapped by the transaction manager. */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransactionSystem(TransactionSystemException ex) {
        if (ex.getRootCause() instanceof ConstraintViolationException violation) {
            return handleConstraintViolation(violation);
        }
        return handleAllUnhandledExceptions(ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return construirErrorResponse(new ErrorResponse(HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido. Verifique o JSON e o formato das datas (AAAA-MM-DD)."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String tipo = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "válido";
        return construirErrorResponse(new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getName() + " deve ser do tipo " + tipo));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUnhandledExceptions(Exception ex) {
        // Spring MVC errors such as 405 or 415 keep their own status instead of becoming 500.
        if (ex instanceof org.springframework.web.ErrorResponse springError) {
            HttpStatus status = HttpStatus.valueOf(springError.getStatusCode().value());
            return construirErrorResponse(new ErrorResponse(status, status.getReasonPhrase()));
        }
        log.error("Erro não tratado", ex);
        return construirErrorResponse(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro desconhecido."));
    }

    private ResponseEntity<ErrorResponse> construirErrorResponse(ErrorResponse errorResponse) {
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }
}
