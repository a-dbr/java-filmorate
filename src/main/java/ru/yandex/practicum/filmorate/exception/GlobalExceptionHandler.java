package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handleEmailAlreadyExists(final EmailAlreadyExistsException e) {
        log.error("EmailAlreadyExistsException: {}", e.getMessage());
        Map<String,String> body = Collections.singletonMap("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidJsonFieldException.class)
    public ResponseEntity<Map<String,String>> handleInvalidJsonField(final InvalidJsonFieldException e) {
        log.error("InvalidJsonFieldException: {} ", e.getMessage());
        Map<String,String> body = Collections.singletonMap("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> handleIllegalArgument(final IllegalArgumentException e) {
        log.error("IllegalArgumentException: {} ", e.getMessage());
        Map<String,String> body = Collections.singletonMap("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,String>> handleNotFound(final NotFoundException e) {
        log.error("NotFoundException: {}", e.getMessage());
        Map<String,String> body = Collections.singletonMap("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<Map<String,String>> handleOperationNotAllowed(final OperationNotAllowedException e) {
        log.error("OperationNotAllowedException: {}", e.getMessage());
        Map<String,String> body = Collections.singletonMap("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // Обработка ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidation(final MethodArgumentNotValidException e) {
        // Собираем все сообщения ошибок валидации
        List<String> errors = e.getBindingResult()
                .getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.error("Validation errors: {}", errors);
        Map<String,List<String>> body = Collections.singletonMap("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    // Обработка остальных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleAll(final Exception e) {
        log.error("Unexpected exception: {}", e.getMessage(), e);
        Map<String,String> body = Collections.singletonMap("error", "Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
