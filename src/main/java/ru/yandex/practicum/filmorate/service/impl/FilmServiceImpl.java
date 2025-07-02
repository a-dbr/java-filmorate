package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.InvalidJsonFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.OperationNotAllowedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    @Override
    @Transactional
    public void addLike(int filmId, int userId) {
        validateFilmExists(filmId);
        validateUserExists(userId);
        if (filmRepository.isLikeExists(filmId, userId)) {
            throw new OperationNotAllowedException("Лайк уже установлен!");
        }
        filmRepository.addLike(filmId, userId);
    }

    @Override
    @Transactional
    public Film createFilm(Film film) {
        if (film.getId() != 0) {
            throw new InvalidJsonFieldException("ID не должен быть указан при создании фильма.");
        }
        validateMpaId(film);
        validateGenreId(film);
        return filmRepository.save(film);
    }

    @Override
    @Transactional
    public void deleteLike(int filmId, int userId) {
        validateFilmExists(filmId);
        validateUserExists(userId);
        filmRepository.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    @Override
    public Film getFilmById(int filmId) {
        validateFilmExists(filmId);
        return filmRepository.findById(filmId).get();
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
        validateFilmExists(film.getId());
        validateMpaId(film);
        validateGenreId(film);
        return filmRepository.save(film);
    }

    // вспомогательные методы валидации
    private void validateFilmExists(int filmId) {
        if (filmRepository.findById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
    }

    private void validateUserExists(int userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }

    private void validateMpaId(Film film) {
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            Optional<Mpa> mpaOpt = mpaRepository.findById(mpaId);
            if (mpaOpt.isEmpty()) {
                throw new NotFoundException("MPA с ID = " + mpaId + " не найден.");
            }
        }
    }

    private void validateGenreId(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                int id = genre.getId();
                if (genreRepository.findById(id).isEmpty()) {
                    throw new NotFoundException("Жанр с ID = " + id + " не найден.");
                }
            }
        }
    }
}
