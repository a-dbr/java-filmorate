package ru.yandex.practicum.filmorate.service.interfaces;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film createFilm(Film film);

    List<Film> getAllFilms();

    Film updateFilm(Film film);
}
