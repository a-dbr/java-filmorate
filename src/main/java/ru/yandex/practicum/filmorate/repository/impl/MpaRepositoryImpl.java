package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.interfaces.MpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaRepositoryImpl implements MpaRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Mpa> mpaMapper;

    @Override
    public List<Mpa> findAll() {
        String sql = "SELECT id, name FROM content_rating ORDER BY id";
        return jdbcTemplate.query(sql, mpaMapper);
    }

    @Override
    public Optional<Mpa> findById(int id) {
        String sql = "SELECT id, name FROM content_rating WHERE id = ?";
        List<Mpa> result = jdbcTemplate.query(sql, mpaMapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    // получить рейтинг по mpa_id из базы (для FilmMapper)
    @Override
    public Mpa findRatingById(int mpaId) {
        String sql = "SELECT id, name FROM content_rating WHERE id = ?";
        return jdbcTemplate.query(sql, mpaMapper, mpaId)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Возрастной рейтинг с ID =" + mpaId + " не найден.")
                );
    }
}
