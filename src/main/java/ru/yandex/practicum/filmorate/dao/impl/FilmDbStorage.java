package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final MpaDaoImpl mpaDao;

    private final GenreDaoImpl genreDao;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDaoImpl mpaDao, GenreDaoImpl genreDao){
        this.jdbcTemplate=jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }
    

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    private @Valid Film makeFilm(ResultSet rs, int i) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(mpaDao.findMpa(rs.getInt("mpa_id")))
                .build();
    }


    @Override
    public Film saveFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, duration, release_date, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setLong(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
//        Заполнение таблицы film_genre
        if (film.getGenres() != null)
            genreDao.saveGenres(filmId, film.getGenres());
        log.info("Фильм успешно сохранен в таблице films");
        return getFilmById(filmId);
    }


    @Override
    public Boolean contains(long id) {
        return jdbcTemplate.query("SELECT * FROM FILMS WHERE id = ?", ResultSet::next, id);

    }

    @Override
    public Boolean contains(Film film) {
        return jdbcTemplate.query("SELECT * FROM FILMS " +
                        "WHERE NAME = ? AND DESCRIPTION = ? AND DURATION = ? AND RELEASE_DATE = ? AND MPA_ID = ?",
                ResultSet::next, film.getName(), film.getDescription(), film.getDuration(),
                film.getReleaseDate(), film.getMpa().getId());

    }

    @Override
    public Film getFilmById(long filmId) {
        // выполняем запрос к базе данных.
        String sql = "SELECT * FROM FILMS WHERE id = ?";
        Film resFilm = jdbcTemplate.queryForObject(sql, this::makeFilm, filmId);
        if (resFilm != null) {
            resFilm.setGenres(genreDao.getGenresById(filmId));
        }
        return resFilm;
    }


    @Override
    public Film update(Film film) {
        String sqlQuery =
                "MERGE INTO FILMS KEY (id) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId());
        if (film.getGenres() != null) {
            genreDao.deleteGenres(film.getId());
            genreDao.saveGenres(film.getId(), film.getGenres());
        }
        return getFilmById(film.getId());
    }

    @Override
    public void putLike(long film_id, long favId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, film_id, favId);
    }

    @Override
    public void deleteLike(long film_id, long favId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, film_id, favId);
    }

    @Override
    public List<Film> getPopularFilms(long count) {
        String sqlQuery =
                "SELECT DISTINCT f.ID,\n" +
                        " l.USER_ID\n" +
                        "FROM films AS f\n" +
                        "LEFT OUTER JOIN likes AS l ON f.ID = l.FILM_ID\n" +
                        "GROUP BY f.ID\n" +
                        "ORDER BY COUNT(l.USER_ID) DESC\n" +
                        "LIMIT ?";
        List<Long> popularity = jdbcTemplate.query(sqlQuery, this::makeFilmId, count);
        return popularity.stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    private Long makeFilmId(ResultSet rs, int i) throws SQLException {
        return rs.getLong("id");
    }

    @Override
    public int getSize() {
        return 0;
    }
}
