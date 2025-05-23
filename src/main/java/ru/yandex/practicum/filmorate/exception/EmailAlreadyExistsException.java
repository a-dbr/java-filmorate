package ru.yandex.practicum.filmorate.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(final String email) {
        super("Пользователь с email " + email + " уже существует");
    }
}
