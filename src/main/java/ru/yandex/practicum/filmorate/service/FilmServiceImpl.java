package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.InvalidJsonFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.OperationNotAllowedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.interfaces.FilmService;

import java.util.List;

@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addLike(int filmId, int userId) {
        if (filmRepository.findById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        if (!filmRepository.isLikeExists(filmId, userId)) {
            filmRepository.addLike(filmId, userId);
        } else {
            throw new OperationNotAllowedException("Лайк уже установлен!");
        }
    }

    @Override
    @Transactional
    public Film createFilm(Film film) {
        if (film.getId() != 0) {
            throw new InvalidJsonFieldException("ID не должен быть указан при создании фильма.");
        }
        return filmRepository.save(film);
    }

    @Override
    @Transactional
    public void deleteLike(int filmId, int userId) {
        if (filmRepository.findById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        filmRepository.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    @Override
    public List<Film> findMostLikedFilms(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Параметр count должен быть положительным числом");
        }
        return filmRepository.findMostLikedFilms(count);
    }

    @Override
    @Transactional
    public Film updateFilm(Film film) {
        return filmRepository.findById(film.getId())
                .map(existing -> filmRepository.save(film))
                .orElseThrow(() ->
                        new NotFoundException("Обновление невозможно. Фильм с ID " + film.getId() + " не найден."));
    }
}
