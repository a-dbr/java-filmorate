package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmRowMapper = new FilmMapper();

    public FilmRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteAll() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
    }

    public List<Film> findAll() {
        String sql = "SELECT id, name, description, release_date, duration FROM films";
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    public Optional<Film> findById(int id) {
        String sql = "SELECT id, name, description, release_date, duration FROM films WHERE id = ?";
        List<Film> results = jdbcTemplate.query(sql, filmRowMapper, id);
        return results.stream().findFirst();
    }

    /**
     * Метод сохранения фильма.
     * Если id фильма больше нуля, значит фильм присутствует в базе. Вызываем метод обновления.
     * Если id == 0, добавляем фильм в бд.
     */
    public Film save(Film film) {
        if (film.getId() > 0) {
            update(film);
            return film;
        } else {
            return insert(film);
        }
    }

    private Film insert(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            return ps;
        }, keyHolder);
        // используем KeyHolder для присвоения фильму id.
        return film.toBuilder().id(keyHolder.getKey().intValue()).build();
    }

    private void update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getId());
    }
}

