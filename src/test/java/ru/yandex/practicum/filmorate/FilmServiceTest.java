package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.InvalidJsonFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.service.interfaces.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FilmorateApplication.class)
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private FilmRepository filmRepository;

    @BeforeEach
    void beforeEach() {
        filmRepository.deleteAll();
    }

    @AfterEach
    void afterEach() {
        filmRepository.deleteAll();
    }

    @Test
    void createFilm() {
        Film input = Film.builder()
                .id(0)
                .name("Дюна")
                .description("Очередной гудящий фильм Вильнева")
                .releaseDate(LocalDate.of(2021, 10, 22))
                .duration(2000)
                .build();

        Film created = filmService.createFilm(input);
        assertNotEquals(0, created.getId());

        Film fromDb = filmRepository.findById(created.getId()).orElseThrow();
        assertEquals("Дюна", fromDb.getName());
    }

    @Test
    void createFilmWithId() {
        Film bad = Film.builder()
                .id(42)
                .name("Угнать за 60 секунд")
                .description("Легендарный фильм с невыносимо талантливым племянником в главной роли")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();

        assertThrows(InvalidJsonFieldException.class,
                () -> filmService.createFilm(bad));
        assertTrue(filmRepository.findAll().isEmpty());
    }

    @Test
    void getAllFilms() {
        filmService.createFilm(Film.builder()
                .id(0)
                .name("A")
                .description("d")
                .releaseDate(LocalDate.now())
                .duration(90).build());
        filmService.createFilm(Film.builder()
                .id(0)
                .name("B")
                .description("d")
                .releaseDate(LocalDate.now())
                .duration(100).build());

        List<Film> all = filmService.getAllFilms();
        assertEquals(2, all.size());
    }

    @Test
    void updateFilm() {
        Film orig = filmService.createFilm(Film.builder()
                .id(0)
                .name("Orig")
                .description("d")
                .releaseDate(LocalDate.now()).duration(120).build());
        Film modified = orig.toBuilder().name("Updated").build();

        Film updated = filmService.updateFilm(modified);
        assertEquals("Updated", updated.getName());

        Film fromDb = filmRepository.findById(updated.getId()).orElseThrow();
        assertEquals("Updated", fromDb.getName());
    }

    @Test
    void updateNonExistingFilm() {
        Film non = Film.builder()
                .id(9999)
                .name("X").description("d")
                .releaseDate(LocalDate.now()).duration(90).build();

        assertThrows(NotFoundException.class,
                () -> filmService.updateFilm(non));
    }
}
