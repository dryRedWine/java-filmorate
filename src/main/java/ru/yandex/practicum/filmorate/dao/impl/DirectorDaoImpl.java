package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;

    private final FilmDbStorage filmDbStorage;

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate, MpaDao mpaDao, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public Director get(long id) {
        final String sqlQuery = "select ID ,NAME " +
                "FROM DIRECTORS " +
                "where ID = ?";
        final List<Director> directors = jdbcTemplate.query(sqlQuery, this::mapRowToDirector, id);
        if (directors.size() != 1) {
            return null;
        }
        return directors.get(0);
    }

    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT ID, NAME FROM DIRECTORS";

        log.debug("Получены режиссеры из базы данных");
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director create(Director director) {
        String sqlQuery = "insert into DIRECTORS (NAME) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "update DIRECTORS set " +
                "NAME = ? " +
                "where ID = ?";
        jdbcTemplate.update(sqlQuery
                , director.getName()
                , director.getId());
        return director;
    }

    @Override
    public void delete(Director director) {
        long id = director.getId();
        String sqlQuery = "delete from DIRECTORS where ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Film> getSortedFilmsByYearOfDirector(long id) {
        String sqlQuery = "SELECT f.*" +
                "FROM FILMS f " +
                "INNER JOIN FILM_DIRECTORS FD on f.ID = FD.FILM_ID " +
                "WHERE DIRECTOR_ID = ? " +
                "ORDER BY f.RELEASE_DATE";
        return jdbcTemplate.query(sqlQuery, this::makeFilm, id);
    }

    @Override
    public List<Film> getSortedFilmsByLikesOfDirector(long id) {
        String sqlQuery = "SELECT COUNT(*) as RATING, f.ID, f.NAME, f.RELEASE_DATE, f.DESCRIPTION," +
                " f.DURATION, m.ID, m.NAME " +
                "FROM LIKES l JOIN FILMS f ON l.FILM_ID = f.ID JOIN MPA m ON f.MPA_ID = m.ID" +
                " WHERE f.ID IN (SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?)" +
                " GROUP BY f.ID, f.NAME, f.RELEASE_DATE, f.DESCRIPTION, f.DURATION, m.ID, m.NAME" +
                " ORDER BY RATING";
        List<Film> sortedFilms = jdbcTemplate.query(sqlQuery, this::makeFilm, id);

        if (sortedFilms.isEmpty()) {
            sqlQuery = "SELECT f.ID, f.NAME, f.RELEASE_DATE, f.DESCRIPTION, f.DURATION," +
                    " m.ID, m.NAME  " +
                    "FROM FILMS f JOIN MPA m ON f.MPA_ID = m.ID " +
                    "WHERE f.ID IN (SELECT FILM_ID FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?) ";
            sortedFilms = jdbcTemplate.query(sqlQuery, this::makeFilm, id);
        }

        return sortedFilms;
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }


    private @Valid Film makeFilm(ResultSet rs, int i) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(mpaDao.findMpa(rs.getInt("id")))
                .build();
    }
}
