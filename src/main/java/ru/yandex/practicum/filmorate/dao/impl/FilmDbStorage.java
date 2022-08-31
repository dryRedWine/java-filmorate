package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final MpaDao mpaDao;

    private final FilmGenreDao filmGenreDao;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDao mpaDao, FilmGenreDao filmGenreDao){
        this.jdbcTemplate=jdbcTemplate;
        this.mpaDao = mpaDao;
        this.filmGenreDao = filmGenreDao;
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
        film.setId(filmId);
        log.info("Фильм успешно сохранен в таблице films");
        return film;
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
        return jdbcTemplate.queryForObject(sql, this::makeFilm, filmId);
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
            filmGenreDao.deleteFilmGenre(film.getId());
            filmGenreDao.saveFilmGenre(film.getId(), film.getGenres());
        }
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getPopularFilms(long count) {
        String sqlQuery =
                "SELECT DISTINCT f.ID,\n" +
                        " l.USER_ID\n" +
                        "FROM films AS f\n" +
                        "LEFT OUTER JOIN likes AS l ON f.ID = l.FILM_ID\n" +
                        "GROUP BY f.ID, l.USER_ID \n" +
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

    @Override
    public List<Film> getPopularFilmsOrderByGenreYear(Optional<Long> genreId, Optional<Integer> year, long count) {
        String sqlQuery = "SELECT DISTINCT f.id,\n" +
                "                f.name,\n" +
                "                f.description,\n" +
                "                f.release_date,\n" +
                "                f.duration,\n" +
                "                COUNT(l.film_id),\n" +
                "                f.mpa_id,\n" +
                "                m.name\n" +
                "FROM films AS f\n" +
                "         LEFT JOIN mpa AS m ON m.id = f.mpa_id\n" +
                "         LEFT JOIN likes L on F.ID = l.film_id\n" +
                "         LEFT JOIN film_genre AS fg ON f.id = fg.film_id\n" +
                "         LEFT JOIN genres AS g ON fg.genre_id = g.id\n" +
                "         LEFT JOIN film_directors fd on f.id = fd.film_id\n" +
                "         LEFT JOIN directors AS d ON fd.director_id = d.id\n" +
                "WHERE fg.genre_id = ? AND YEAR (f.release_date) = ?\n" +
                "GROUP BY f.id, fg.genre_id, fd.director_id\n" +
                "ORDER BY COUNT(l.film_id) DESC \n" +
                "LIMIT ?";
        List<Long> commonFilms = jdbcTemplate.query(sqlQuery, this::makeFilmId, genreId.get(), year.get(), count);
        return commonFilms.stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getPopularFilmsOrderByGenre(Optional<Long> genreId, long count) {
        String sqlQuery = "SELECT DISTINCT f.id,\n" +
                "                f.name,\n" +
                "                f.description,\n" +
                "                f.release_date,\n" +
                "                f.duration,\n" +
                "                COUNT(l.film_id),\n" +
                "                f.mpa_id,\n" +
                "                m.name\n" +
                "FROM films AS f\n" +
                "         LEFT JOIN mpa AS m ON m.id = f.mpa_id\n" +
                "         LEFT JOIN likes L on F.ID = l.film_id\n" +
                "         LEFT JOIN film_genre AS fg ON f.id = fg.film_id\n" +
                "         LEFT JOIN genres AS g ON fg.genre_id = g.id\n" +
                "         LEFT JOIN film_directors fd on f.id = fd.film_id\n" +
                "         LEFT JOIN directors AS d ON fd.director_id = d.id\n" +
                "WHERE fg.genre_id = ?\n" +
                "GROUP BY f.id, fg.genre_id, fd.director_id\n" +
                "ORDER BY COUNT(l.film_id) DESC \n" +
                "LIMIT ?";
        List<Long> commonFilms = jdbcTemplate.query(sqlQuery, this::makeFilmId, genreId.get(), count);
        return commonFilms.stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getPopularFilmsOrderByYear(Optional<Integer> year, long count) {
        String sqlQuery = "SELECT DISTINCT f.id,\n" +
                "                f.name,\n" +
                "                f.description,\n" +
                "                f.release_date,\n" +
                "                f.duration,\n" +
                "                COUNT(l.film_id),\n" +
                "                f.mpa_id,\n" +
                "                m.name\n" +
                "FROM films AS f\n" +
                "         LEFT JOIN mpa AS m ON m.id = f.mpa_id\n" +
                "         LEFT JOIN likes L on F.ID = l.film_id\n" +
                "         LEFT JOIN film_genre AS fg ON f.id = fg.film_id\n" +
                "         LEFT JOIN genres AS g ON fg.genre_id = g.id\n" +
                "         LEFT JOIN film_directors fd on f.id = fd.film_id\n" +
                "         LEFT JOIN directors AS d ON fd.director_id = d.id\n" +
                "WHERE YEAR (f.release_date) = ?\n" +
                "GROUP BY f.id, fg.genre_id, fd.director_id\n" +
                "ORDER BY COUNT(l.film_id) DESC \n" +
                "LIMIT  ?";
        List<Long> commonFilms = jdbcTemplate.query(sqlQuery, this::makeFilmId);
        return commonFilms.stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }
}
