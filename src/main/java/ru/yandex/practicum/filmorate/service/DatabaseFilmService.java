package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.InvalidJsonFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.service.interfaces.FilmService;

import java.util.List;

@Service
@AllArgsConstructor
public class DatabaseFilmService implements FilmService {
    FilmRepository filmRepository;

    @Override
    @Transactional
    public Film createFilm(Film film) {
        if (film.getId() != 0) {
            throw new InvalidJsonFieldException("ID не должен быть указан при создании фильма.");
        }
        return filmRepository.save(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmRepository.findAll();
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
