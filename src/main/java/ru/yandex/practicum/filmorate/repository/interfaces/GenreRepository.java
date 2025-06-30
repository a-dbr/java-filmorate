package ru.yandex.practicum.filmorate.repository.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository {

    List<Genre> findAll();

    Optional<Genre> findById(int id);

    Set<Genre> findGenresByFilmId(int filmId);
}