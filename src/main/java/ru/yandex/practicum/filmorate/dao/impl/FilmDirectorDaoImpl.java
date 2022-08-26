package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDirectorDao;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
public class FilmDirectorDaoImpl implements FilmDirectorDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmDirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFilmDirector(Film film) {
        Set<Director> directors = film.getDirectors();
        String sqlQuery = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES ( ?, ? )";
        for (Director director : directors) {
            jdbcTemplate.update(sqlQuery, film.getId(), director.getId());
            log.debug("Для фильма {} записан режиссер {}", film.getId(), director.getId());
        }
    }

    @Override
    public void setFilmDirector(Film film) {
        String sqlQuery = "SELECT d.ID," +
                "                 d.NAME " +
                "FROM FILM_DIRECTORS AS fd " +
                "INNER JOIN DIRECTORS d on d.ID = fd.DIRECTOR_ID " +
                "WHERE fd.FILM_ID = ?";
        List<Director> directors = jdbcTemplate.query(sqlQuery, this::mapRowToDirector, film.getId());
        Set<Director> directorsSet = new HashSet<>(directors);
        film.setDirectors(directorsSet);
    }

    @Override
    public void saveFilmDirector(long filmId, Set<Director> directors) {
        String sqlQuery = "MERGE INTO FILM_DIRECTORS KEY (film_id, DIRECTOR_ID) VALUES (?, ?)";
        for (Director director : directors) {
            jdbcTemplate.update(sqlQuery, filmId, director.getId());
        }
    }

    @Override
    public Set<Director> getFilmDirectors(Film film) {
        Set<Director> directors;
        String sqlQuery = "SELECT d.ID," +
                "                 d.NAME " +
                "FROM FILM_DIRECTORS AS fd " +
                "INNER JOIN DIRECTORS d on d.ID = FD.DIRECTOR_ID " +
                "WHERE fd.FILM_ID = ?";
        directors = new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToDirector, film.getId()));
        return directors;
    }

    @Override
    public void deleteFilmDirector(long id) {
        String sqlQuery = "DELETE FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        sqlQuery = "DELETE FROM DIRECTORS WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.debug("Удалён режиссер с id: {}", id);

    }

    @Override
    public Film updateFilmDirector(Film film) {
        jdbcTemplate.update("DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ?", film.getId());
        String sqlQuery = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES ( ?, ?)";

        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(sqlQuery,
                    film.getId(),
                    director.getId()
            );
        }
        log.debug("Режиссеры фильма {} обновлены в базе данных", film);
        return film;
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
