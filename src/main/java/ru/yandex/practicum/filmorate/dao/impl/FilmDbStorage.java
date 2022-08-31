package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.model.Film;

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

    private final FilmDirectorDao filmDirectorDao;
    private final FilmGenreDao filmGenreDao;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         MpaDao mpaDao,
                         FilmGenreDao filmGenreDao,
                         FilmDirectorDao filmDirectorDao){
        this.jdbcTemplate=jdbcTemplate;
        this.mpaDao = mpaDao;
        this.filmGenreDao = filmGenreDao;
        this.filmDirectorDao = filmDirectorDao;
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
            filmGenreDao.saveFilmGenre(filmId, film.getGenres());
        //        Заполнение таблицы film_directors
        if (film.getDirectors() != null)
            filmDirectorDao.saveFilmDirector(filmId, film.getDirectors());
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
            resFilm.setGenres(filmGenreDao.getFilmGenreById(filmId));
            filmDirectorDao.setFilmDirector(resFilm);
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

    @Override
    public List<Film> searchFilmsByTitle(String query) {
        log.info("log перед выполнением запроса к бд");
        // Не забыть про пробелы!
        String sql = "SELECT DISTINCT f.ID, " +
                "COUNT(l.USER_ID) AS count_likes " +
                "FROM films AS f " +
                "LEFT OUTER JOIN likes AS l ON f.ID = l.FILM_ID " +
                "WHERE f.name ILIKE ? " +
                "GROUP BY f.ID " +
                "ORDER BY count_likes DESC";
        List<Long> filmsByQuery = jdbcTemplate.query(sql, this::makeFilmId, '%' + query + '%');
        return filmsByQuery.stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> searchFilmsByDirector(String query) {
        log.info("log перед выполнением запроса к бд");
        // Не забыть про пробелы!
        String sql = "SELECT DISTINCT fd.FIlM_ID       AS id, " +
                "                COUNT(l.USER_ID) AS count_likes " +
                "FROM directors AS d\n" +
                "         LEFT OUTER JOIN film_directors AS fd ON d.ID = fd.director_id\n" +
                "         LEFT OUTER JOIN likes AS l ON id = l.FILM_ID\n" +
                "WHERE d.name ILIKE ?\n" +
                "GROUP BY id\n" +
                "ORDER BY count_likes DESC;";
        List<Long> filmsByQuery = jdbcTemplate.query(sql, this::makeFilmId, '%' + query + '%');
        return filmsByQuery.stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> searchFilmsByDirectorOrTitle(String query) {
        log.info("log перед выполнением запроса к бд");
        // Не забыть про пробелы!
        String sql = "SELECT DISTINCT f.ID,\n" +
                "                COUNT(l.USER_ID) AS count_likes\n" +
                "FROM FILMS AS f\n" +
                "         LEFT OUTER JOIN film_directors AS fd ON f.ID = fd.FILM_ID\n" +
                "         LEFT OUTER JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.ID\n" +
                "         LEFT OUTER JOIN likes AS l ON f.ID = l.FILM_ID\n" +
                "WHERE d.name ILIKE ?\n" +
                "   OR f.name ILIKE ?\n" +
                "GROUP BY f.ID\n" +
                "ORDER BY count_likes DESC";
        List<Long> filmsByQuery = jdbcTemplate.query(
                sql,
                this::makeFilmId,
                '%' + query + '%',
                '%' + query + '%');
        return filmsByQuery.stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
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
                "WHERE fg.genre_id = ? AND YEAR (f.release_date)  ?\n" +
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
                "WHERE fg.genre_id  ?\n" +
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
                "WHERE YEAR (f.release_date)  ?\n" +
                "GROUP BY f.id, fg.genre_id, fd.director_id\n" +
                "ORDER BY COUNT(l.film_id) DESC \n" +
                "LIMIT  ?";
        List<Long> commonFilms = jdbcTemplate.query(sqlQuery, this::makeFilmId);
        return commonFilms.stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }
}
