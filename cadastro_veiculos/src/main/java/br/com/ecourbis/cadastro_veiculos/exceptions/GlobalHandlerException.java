package br.com.ecourbis.cadastro_veiculos.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        String mensagemErro = "Erro durante a execução: " + e.getMessage();
        return new ResponseEntity<>(mensagemErro, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegratityException(DataIntegrityViolationException e) {
        String mensagemErro = "Erro de integridade de dados: " + e.getMessage();
        return new ResponseEntity<>(mensagemErro, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
        String mensagemErro = "Entidade não encontrada: " + e.getMessage();
        return new ResponseEntity<>(mensagemErro, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Manipula exceções do tipo MethodArgumentNotValidException
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        // Obtém as mensagens de erro de validação e as concatena em uma única string
        String mensagemErro = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage) // Mapeia cada erro de campo para sua mensagem de erro
                .collect(Collectors.joining(", ")); // Coleta todas as mensagens de erro em uma única string separada por vírgulas

        // Retorna uma resposta com status HTTP 400 (Bad Request) e as mensagens de erro de validação no corpo da resposta
        return ResponseEntity.badRequest().body(mensagemErro);
    }
}