package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilmMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        int filmId = rs.getInt("id");

        Set<String> genres = getGenresForFilm(filmId);

        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date") != null
                        ? rs.getDate("release_date").toLocalDate()
                        : null)
                .duration(rs.getInt("duration"))
                .genre(genres)
                .contentRatingId(rs.getInt("content_rating_id"))
                .build();
    }

    private Set<String> getGenresForFilm(int filmId) {
        String sql = """
            SELECT g.name
            FROM film_genres fg
            JOIN genres g ON fg.genre_id = g.id
            WHERE fg.film_id = ?
        """;

        return new HashSet<>(jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("name"),
                filmId
        ));
    }
}
