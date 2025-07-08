package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilmMapper implements RowMapper<Film> {
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    @Override
    public Film mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        int filmId = rs.getInt("id");
        int contentRatingId = rs.getInt("content_rating_id");
        Set<Genre> genres = genreRepository.findGenresByFilmId(filmId);
        Mpa contentRating = mpaRepository.findRatingById(contentRatingId);

        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date") != null
                        ? rs.getDate("release_date").toLocalDate()
                        : null)
                .duration(rs.getInt("duration"))
                .genres(genres)
                .mpa(contentRating)
                .build();
    }
}