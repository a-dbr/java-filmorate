package ru.yandex.practicum.filmorate.repository.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    void addLike(int filmId, int userId);

    void deleteAll();

    void deleteLike(int filmId, int userId);

    List<Film> findAll();

    Optional<Film> findById(int id);

    List<Film> findMostLikedFilms(int count);

    boolean isLikeExists(int filmId, int userId);

    Film save(Film film);
}