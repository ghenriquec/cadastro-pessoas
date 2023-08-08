package com.elotech.Exceptions;

import com.elotech.Response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return construirErrorResponse(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> erros = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> erros.add(error.getDefaultMessage()));
        return construirErrorResponse(new ErrorResponse(HttpStatus.BAD_REQUEST, "Falha na validação.", erros));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String erro = ex.getName() + " deve ser do tipo " + ex.getRequiredType().getName();
        return construirErrorResponse(new ErrorResponse(HttpStatus.BAD_REQUEST, erro));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUnhandledExceptions(Exception ex) {
        return construirErrorResponse(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro desconhecido."));
    }

    private ResponseEntity<ErrorResponse> construirErrorResponse(ErrorResponse ErrorResponse) {
        return new ResponseEntity<>(ErrorResponse, HttpStatus.valueOf(ErrorResponse.getStatus()));
    }
}
