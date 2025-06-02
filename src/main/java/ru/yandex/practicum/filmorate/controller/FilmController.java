package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.interfaces.FilmService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private FilmService filmService;

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.findMostLikedFilms(count);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }
}