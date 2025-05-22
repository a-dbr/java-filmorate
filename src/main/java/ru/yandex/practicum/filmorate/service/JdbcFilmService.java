package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidJsonFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.service.interfaces.FilmService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JdbcFilmService implements FilmService {
    FilmRepository filmRepository;

    @Override
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
    public Film updateFilm(Film film) {
        Optional<Film> updatedFilm = filmRepository.findById(film.getId());
        if (updatedFilm.isPresent()) {
            return filmRepository.save(film);
        } else {
            throw new NotFoundException("Обновление невозможно. Фильм с ID " + film.getId() + " не найден.");
        }
    }
}
