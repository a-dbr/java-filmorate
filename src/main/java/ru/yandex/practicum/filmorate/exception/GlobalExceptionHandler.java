package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(final EmailAlreadyExistsException e) {
        log.error("EmailAlreadyExistsException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InvalidJsonFieldException.class)
    public ResponseEntity<String> handleInvalidJsonFieldException(final InvalidJsonFieldException e) {
        log.error("InvalidJsonFieldException: {} ", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // Обработка ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(final MethodArgumentNotValidException e) {
        // Собираем все сообщения ошибок валидации
        String errors = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.error("Validation errors: {}", errors);
        return ResponseEntity.badRequest().body("Ошибки валидации: " + errors);
    }

    // Обработка остальных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(final RuntimeException e) {
        log.error(e.getMessage());
        // Возвращаем HTTP 500 с сообщением
        return new ResponseEntity<>("Произошла ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
