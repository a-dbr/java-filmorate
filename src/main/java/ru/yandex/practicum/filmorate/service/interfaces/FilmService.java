package ru.yandex.practicum.filmorate.service.interfaces;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    void addLike(int filmId, int userId);

    Film createFilm(Film film);

    void deleteLike(int filmId, int userId);

    List<Integer> findMostLikedFilms(int count);

    List<Film> getAllFilms();

    Film updateFilm(Film film);
}
