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
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final LikesDao likesDao;

    private final FilmDirectorDao filmDirectorDao;

    private final DirectorDao directorDao;

    private final FilmGenreDao filmGenreDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage inMemoryFilmStorage,
                       LikesDao likesDao,
                       FilmDirectorDao filmDirectorDao,
                       DirectorDao directorDao,
                       FilmGenreDao filmGenreDao,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = inMemoryFilmStorage;
        this.likesDao = likesDao;
        this.filmDirectorDao = filmDirectorDao;
        this.directorDao = directorDao;
        this.filmGenreDao = filmGenreDao;
        this.userStorage = userStorage;
    }

    private static final LocalDate DATE = LocalDate.of(1895, 12, 28);

    public Film create(Film film)
            throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        if (film.getReleaseDate().isBefore(DATE))
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        if (!filmStorage.contains(film)) {
            log.info("Данный фильм добавлен");
            return filmStorage.saveFilm(film);
        } else {
            log.error("Данный фильм уже добавлен");
            throw new AlreadyExistException("Данный фильм уже добавлен");
        }
    }

    public Film update(Film film) throws NegativeIdException {
        CheckForId.idCheck(film.getId());
        if (film.getReleaseDate().isBefore(DATE))
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        filmStorage.update(film);
        log.info("Данные о фильме добавлены или обновлены");
        return film;
    }

    public List<Film> get() {
//        log.info("Текущее количество добавленных фильмов: {}", filmStorage.getSize());
        List<Film> films = filmStorage.findAll();

        for (Film film : films) {
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
        return filmStorage.getFilmById(id);
    }

    public void putLikeToFilm(Long film_id, Long favId) throws NegativeIdException {
        CheckForId.idCheck(film_id, favId);
        if (filmStorage.contains(film_id) && userStorage.contains(favId)) {
            likesDao.putLike(film_id, favId);
            log.info("+1 лайк");
        } else
            throw new InvalidIdInPathException("Ошибка один из пользователей не существует");
    }

    public void deleteLikeToFilm(Long film_id, Long hateId) throws NegativeIdException {
        CheckForId.idCheck(film_id, hateId);
        if (filmStorage.contains(film_id) && userStorage.contains(hateId)) {
            likesDao.deleteLike(film_id, hateId);
            log.info("-1 лайк");
        }
    }

    public List<Film> getPopularFilms(Long count) throws NegativeIdException {
        log.info("Вывод рейтинга фильмов по количеству лайков");
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        if (userStorage.contains(userId) & userStorage.contains(friendId)) {
            log.info("Cписок фильмов, отсортированных по популярности.");
            return filmStorage.getCommonFilms(userId, friendId);
        } else throw new InvalidIdInPathException("Ошибка один из пользователей не существует");
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

    public Collection<Film> searchFilms(String query, List<String> by) {
        if (by.size() == 1) {
            if (by.contains("title"))
                return filmStorage.searchFilmsByTitle(query);
            else if (by.contains("director"))
                return filmStorage.searchFilmsByDirector(query);
            else throw new InvalidIdInPathException("Передан некорректный параметр запроса");
        } else if (by.size() == 2)
            return filmStorage.searchFilmsByDirectorOrTitle(query);
        else
            throw new InvalidIdInPathException("Передан некорректный параметр запроса");
    }
}
