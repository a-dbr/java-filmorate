package ru.yandex.practicum.filmorate.exception;

public class InvalidJsonFieldException extends RuntimeException {
    public InvalidJsonFieldException(final String message) {
        super(message);
    }
}
