package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryImpl implements GenreRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> genreMapper;

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT id, name FROM genres ORDER BY id";
        return jdbcTemplate.query(sql, genreMapper);
    }

    @Override
    public Optional<Genre> findById(int id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        List<Genre> result = jdbcTemplate.query(sql, genreMapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    // Получить жанры по film_id (для FilmMapper)
    @Override
    public Set<Genre> findGenresByFilmId(int filmId) {
        String sql = "SELECT g.id, g.name " +
                "FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ? ORDER BY g.id";
        List<Genre> genres = jdbcTemplate.query(sql, genreMapper, filmId);
        return new HashSet<>(genres);
    }
}