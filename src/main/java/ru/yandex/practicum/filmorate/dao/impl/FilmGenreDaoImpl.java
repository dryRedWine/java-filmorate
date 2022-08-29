package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class FilmGenreDaoImpl implements FilmGenreDao {

    private final JdbcTemplate jdbcTemplate;

    private final GenreDaoImpl genreDaoImpl;

    public FilmGenreDaoImpl(JdbcTemplate jdbcTemplate, GenreDaoImpl genreDaoImpl) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDaoImpl = genreDaoImpl;
    }

    @Override
    public void saveFilmGenre(long filmId, LinkedHashSet<Genre> genres) {
        String sqlQuery = "MERGE INTO FILM_GENRE KEY (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(sqlQuery, filmId, genre.getId());
        }
    }

    @Override
    public void deleteFilmGenre(long filmId) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? ";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public LinkedHashSet<Genre> getFilmGenreById(long film_id) {
        String sqlQuery = "SELECT genre_id FROM film_genre WHERE film_id = ? ORDER BY GENRE_ID";
        List<Long> genresId = jdbcTemplate.query(sqlQuery, this::makeGenreId, film_id);
        return genresId.stream().map(genreDaoImpl::findGenre).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Long makeGenreId(ResultSet rs, int i) throws SQLException {
        return rs.getLong("genre_id");
    }
}
