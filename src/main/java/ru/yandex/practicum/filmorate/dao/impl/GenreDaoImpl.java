package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre findGenre(long genreId) {
        CheckForId.idCheck(genreId);
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId);
    }

    private @Valid Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder().id(rs.getInt("id")).name(rs.getString("name")).build();
    }

    @Override
    public LinkedHashSet<Genre> findAll() {
        String sqlQuery = "SELECT * FROM genres";
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToGenre));
    }

    @Override
    public void saveGenres(long filmId, LinkedHashSet<Genre> genres) {
        String sqlQuery = "MERGE INTO FILM_GENRE KEY (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres)
            jdbcTemplate.update(sqlQuery, filmId, genre.getId());
    }

    @Override
    public void deleteGenres(long filmId) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? ";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public LinkedHashSet<Genre> getGenresById(long film_id) {
        String sqlQuery = "SELECT genre_id FROM film_genre WHERE film_id = ? ORDER BY GENRE_ID";
        List<Long> genresId = jdbcTemplate.query(sqlQuery, this::makeGenreId, film_id);
        return genresId.stream().map(this::findGenre).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Long makeGenreId(ResultSet rs, int i) throws SQLException {
        return rs.getLong("genre_id");
    }

}
