package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.InvalidJsonFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.OperationNotAllowedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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

    @Test
    void addLike() {
        // Создаем пользователя и фильм
        User user = User.builder()
                .id(0)
                .email("test@user.ru")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User savedUser = userRepository.save(user);

        Film film = Film.builder()
                .id(0)
                .name("Test Film")
                .description("Test Desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();
        Film savedFilm = filmRepository.save(film);

        filmService.addLike(savedFilm.getId(), savedUser.getId());
        assertTrue(filmRepository.isLikeExists(savedFilm.getId(), savedUser.getId()));
    }

    @Test
    void addLikeFilmNotFound() {
        User user = User.builder()
                .id(0)
                .email("test@user.ru")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User savedUser = userRepository.save(user);

        assertThrows(NotFoundException.class,
                () -> filmService.addLike(9999, savedUser.getId()));
    }

    @Test
    void addLikeUserNotFound() {
        Film film = Film.builder()
                .id(0)
                .name("Test Film")
                .description("Test Desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();
        Film savedFilm = filmRepository.save(film);

        assertThrows(NotFoundException.class,
                () -> filmService.addLike(savedFilm.getId(), 9999));
    }

    @Test
    void addLikeTwice() {
        User user = User.builder()
                .id(0)
                .email("test@user.ru")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User savedUser = userRepository.save(user);

        Film film = Film.builder()
                .id(0)
                .name("Test Film")
                .description("Test Desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();
        Film savedFilm = filmRepository.save(film);

        filmService.addLike(savedFilm.getId(), savedUser.getId());
        assertThrows(OperationNotAllowedException.class,
                () -> filmService.addLike(savedFilm.getId(), savedUser.getId()));
    }

    @Test
    void deleteLike() {
        User user = User.builder()
                .id(0)
                .email("test@user.ru")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User savedUser = userRepository.save(user);

        Film film = Film.builder()
                .id(0)
                .name("Test Film")
                .description("Test Desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();
        Film savedFilm = filmRepository.save(film);

        filmService.addLike(savedFilm.getId(), savedUser.getId());
        assertTrue(filmRepository.isLikeExists(savedFilm.getId(), savedUser.getId()));

        filmService.deleteLike(savedFilm.getId(), savedUser.getId());
        assertFalse(filmRepository.isLikeExists(savedFilm.getId(), savedUser.getId()));
    }

    @Test
    void findMostLikedFilms() {
        User user1 = User.builder()
                .id(0)
                .email("u1@user.ru")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User user2 = User.builder()
                .id(0)
                .email("u2@user.ru")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Film film1 = filmRepository.save(Film.builder()
                .id(0)
                .name("Film1")
                .description("d1")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(100)
                .build());
        Film film2 = filmRepository.save(Film.builder()
                .id(0)
                .name("Film2")
                .description("d2")
                .releaseDate(LocalDate.of(2002, 2, 2))
                .duration(110)
                .build());

        // film1 получает 2 лайка, film2 — 1 лайк
        filmService.addLike(film1.getId(), savedUser1.getId());
        filmService.addLike(film1.getId(), savedUser2.getId());
        filmService.addLike(film2.getId(), savedUser1.getId());

        List<Film> top = filmService.findMostLikedFilms(2);
        assertEquals(2, top.size());
        assertEquals(film1.getId(), top.get(0).getId());
        assertEquals(film2.getId(), top.get(1).getId());
    }

    @Test
    void findMostLikedFilmsInvalidCount() {
        assertThrows(IllegalArgumentException.class,
                () -> filmService.findMostLikedFilms(0));
        assertThrows(IllegalArgumentException.class,
                () -> filmService.findMostLikedFilms(-1));
    }
}
