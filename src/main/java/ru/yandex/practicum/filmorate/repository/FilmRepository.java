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
    private final RowMapper<Film> filmRowMapper;

    public FilmRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        filmRowMapper = new FilmMapper(jdbcTemplate);
    }

    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);

    }

    public void deleteAll() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
    }

    public void deleteLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    public Optional<Film> findById(int id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> results = jdbcTemplate.query(sql, filmRowMapper, id);
        return results.stream().findFirst();
    }

    public List<Film> findMostLikedFilms(int count) {
        String sql = """
        SELECT f.*
        FROM films f
        LEFT JOIN likes l ON f.id = l.film_id
        GROUP BY f.id
        ORDER BY COUNT(l.user_id) DESC
        LIMIT ?
        """;
        return jdbcTemplate.query(sql, filmRowMapper, count);
    }

    public boolean isLikeExists(int filmId, int userId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM likes WHERE film_id = ? AND user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, filmId, userId));
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
        String sql = "INSERT INTO films (name, description, release_date, duration, content_rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getContentRatingId());
            return ps;
        }, keyHolder);
        // используем KeyHolder для присвоения фильму id.
        return film.toBuilder().id(keyHolder.getKey().intValue()).build();
    }

    private void update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "content_rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getContentRatingId(), film.getId());
    }
}

