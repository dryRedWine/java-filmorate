package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final LikesDao likesDao;

    private final FilmDirectorDao filmDirectorDao;

    private final DirectorDao directorDao;

    private final FilmGenreDao filmGenreDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage inMemoryFilmStorage, LikesDao likesDao, FilmDirectorDao filmDirectorDao, DirectorDao directorDao, FilmGenreDao filmGenreDao) {
        this.filmStorage = inMemoryFilmStorage;
        this.likesDao = likesDao;
        this.filmDirectorDao = filmDirectorDao;
        this.directorDao = directorDao;
        this.filmGenreDao = filmGenreDao;
    }

    private static final LocalDate DATE = LocalDate.of(1895, 12, 28);

    public Film create(Film film)
            throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        if (film.getReleaseDate().isBefore(DATE)) {
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        }
        if (!filmStorage.contains(film)) {
            log.info("Данный фильм добавлен");
            film = filmStorage.saveFilm(film);
            if (film.getGenres() != null) {
                filmGenreDao.saveFilmGenre(film.getId(), film.getGenres());
            }
            if (film.getDirectors() != null) {
                filmDirectorDao.saveFilmDirector(film.getId(), film.getDirectors());
            }
            return film;
        } else {
            log.error("Данный фильм уже добавлен");
            throw new AlreadyExistException("Данный фильм уже добавлен");
        }
    }

    public Film update(Film film) throws NegativeIdException {
        CheckForId.idCheck(film.getId());
        if (film.getReleaseDate().isBefore(DATE)) {
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        }
        filmStorage.update(film);
        log.info("Данные о фильме добавлены или обновлены");
        if (film.getDirectors() != null) {
            filmDirectorDao.updateFilmDirector(film);
        }
        return film;
    }

    public List<Film> get() {
//        log.info("Текущее количество добавленных фильмов: {}", filmStorage.getSize());
        List<Film> films = filmStorage.findAll();

        for(Film film: films) {
            long id = film.getId();
            film.setGenres(filmGenreDao.getFilmGenreById(id));
            filmDirectorDao.setFilmDirector(film);
        }

        return films;
    }

    public Film getFilmById(Long id) throws NegativeIdException {
        CheckForId.idCheck(id);
        if (!filmStorage.contains(id)) {
            log.error("Данный пользователь не существует");
            throw new InvalidIdInPathException("Данный пользователь не существует");
        }
        log.info("Заданный пользователь успешно возвращен");
        Film film = filmStorage.getFilmById(id);
        film.setGenres(filmGenreDao.getFilmGenreById(id));
        filmDirectorDao.setFilmDirector(film);
        return film;
    }

    public void putLikeToFilm(Long film_id, Long favId) throws NegativeIdException {
        CheckForId.idCheck(film_id, favId);
        likesDao.putLike(film_id, favId);
        log.info("+1 лайк");
    }

    public void deleteLikeToFilm(Long film_id, Long hateId) throws NegativeIdException {
        CheckForId.idCheck(film_id, hateId);
        likesDao.deleteLike(film_id, hateId);
        log.info("-1 лайк");
    }

    public List<Film> getPopularFilms(Long count) throws NegativeIdException {
        log.info("Вывод рейтинга фильмов по количеству лайков");
        return filmStorage.getPopularFilms(count);
    }



    public List<Film> findAllFilmsOfDirectorSorted(long id, String sortBy) {
        final Director director = directorDao.get(id);
        if (director == null) {
            throw new InvalidIdInPathException("Director with id=" + id + "not found");
        }

        List<Film> sortedFilms;
        if ("year".equals(sortBy)) {
            sortedFilms = directorDao.getSortedFilmsByYearOfDirector(id);
        } else if ("likes".equals(sortBy)) {
            sortedFilms = directorDao.getSortedFilmsByLikesOfDirector(id);
        } else {
            return null;
        }
        for (Film film : sortedFilms) {
            film.setGenres(filmGenreDao.getFilmGenreById(film.getId()));
            filmDirectorDao.setFilmDirector(film);
        }
        return sortedFilms;
    }

    public List<Film> getPopularFilmsOrderByGenreYear(Optional<Long> genreId, Optional<Integer> year, long count) {
        if (genreId.isPresent() && year.isPresent()) {
            log.info("Вывод фильмов, по жанру и году.");
            return filmStorage.getPopularFilmsOrderByGenreYear(genreId, year, count);
        }
        if (genreId.isEmpty() && year.isEmpty()) {
            log.info("Вывод рейтинга фильмов по количеству лайков");
            return filmStorage.getPopularFilms(count);
        }
        if (genreId.isPresent()) {
            log.info("Вывод рейтинга фильмов по жанру");
            return filmStorage.getPopularFilmsOrderByGenre(genreId, count);
        }
        log.info("Вывод рейтинга фильмов по году");
        return filmStorage.getPopularFilmsOrderByYear(year, count);
    }
}
