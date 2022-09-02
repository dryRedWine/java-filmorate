package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dao.impl.EventDaoImpl;
import ru.yandex.practicum.filmorate.enums.SearchName;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.model.eventEnum.EventType;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.enums.SearchName.DIRECTOR;
import static ru.yandex.practicum.filmorate.enums.SearchName.TITLE;
import static ru.yandex.practicum.filmorate.enums.SortParam.YEAR;
import static ru.yandex.practicum.filmorate.enums.SortParam.LIKES;

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
    private final EventDaoImpl eventDaoImpl;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage inMemoryFilmStorage,
                       LikesDao likesDao,
                       FilmDirectorDao filmDirectorDao,
                       DirectorDao directorDao,
                       FilmGenreDao filmGenreDao,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       EventDaoImpl eventDaoImpl) {
        this.filmStorage = inMemoryFilmStorage;
        this.likesDao = likesDao;
        this.filmDirectorDao = filmDirectorDao;
        this.directorDao = directorDao;
        this.filmGenreDao = filmGenreDao;
        this.userStorage = userStorage;
        this.eventDaoImpl = eventDaoImpl;
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
            log.warn("Данный фильм уже добавлен");
            throw new AlreadyExistException("Данный фильм уже добавлен");
        }
    }

    public Film update(Film film) throws NegativeIdException {
        CheckForId.idCheck(film.getId());
        if (film.getReleaseDate().isBefore(DATE))
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        filmStorage.update(film);
        log.info("Данные о фильме добавлены или обновлены");
        if (film.getDirectors() != null) {
            filmDirectorDao.updateFilmDirector(film);
        }
        return film;
    }

    public List<Film> get() {
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
            log.warn("Данный пользователь не существует");
            throw new InvalidIdInPathException("Данный пользователь не существует");
        }
        log.info("Заданный пользователь успешно возвращен");
        return filmStorage.getFilmById(id);
    }

    public void putLikeToFilm(Long film_id, Long favId) throws NegativeIdException {
        CheckForId.idCheck(film_id);
        CheckForId.idCheck(favId);
        if (filmStorage.contains(film_id) && userStorage.contains(favId)) {
            if(likesDao.contains(film_id, favId)) {
                eventDaoImpl.addEvent(favId, EventType.LIKE, EventOperation.ADD, film_id);
            } else {
                likesDao.putLike(film_id, favId);
                log.info("+1 лайк");
                eventDaoImpl.addEvent(favId, EventType.LIKE, EventOperation.ADD, film_id);
            }
        } else
            throw new InvalidIdInPathException("Ошибка один из пользователей не существует");
    }

    public void deleteLikeToFilm(Long film_id, Long hateId) throws NegativeIdException {
        CheckForId.idCheckEquals(film_id, hateId);
        if (filmStorage.contains(film_id) && userStorage.contains(hateId)) {
            likesDao.deleteLike(film_id, hateId);
            log.info("-1 лайк");
            eventDaoImpl.addEvent(hateId, EventType.LIKE, EventOperation.REMOVE, film_id);
        }
    }

    public List<Film> getPopularFilms(Long count) throws NegativeIdException {
        log.info("Вывод рейтинга фильмов по количеству лайков");
        return filmStorage.getPopularFilms(count);
    }


    public void deleteFilm(Long film_id) throws NegativeIdException {
        CheckForId.idCheck(film_id);
        filmStorage.deleteFilm(film_id);
        log.info("-1 фильм");
    }


    public List<Film> getCommonFilms(long userId, long friendId) {
        if (userStorage.contains(userId) & userStorage.contains(friendId)) {
            log.info("Список фильмов, отсортированных по популярности.");
            return filmStorage.getCommonFilms(userId, friendId);
        } else throw new InvalidIdInPathException("Ошибка один из пользователей не существует");
    }



    public List<Film> findAllFilmsOfDirectorSorted(long id, String sortBy) {
        final Director director = directorDao.get(id);
        if (director == null) {
            throw new InvalidIdInPathException("Director with id=" + id + "not found");
        }

        List<Film> sortedFilms;
        if (YEAR.getParam().equals(sortBy)) {
            sortedFilms = directorDao.getSortedFilmsByYearOfDirector(id);
        } else if (LIKES.getParam().equals(sortBy)) {
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
            if (by.contains(TITLE.getName()))
                return filmStorage.searchFilmsByTitle(query);
            else if (by.contains(DIRECTOR.getName()))
                return filmStorage.searchFilmsByDirector(query);
            else throw new InvalidIdInPathException("Передан некорректный параметр запроса");
        } else if (by.size() == 2)
            return filmStorage.searchFilmsByDirectorOrTitle(query);
        else
            throw new InvalidIdInPathException("Передан некорректный параметр запроса");
    }

    public List<Film> getPopularFilmsOrderByGenreYear(Optional<Long> genreId, Optional<Integer> year, long count) {
        if (genreId.isPresent() && year.isPresent()) {
            log.info("Вывод фильмов, по жанру и году.");
            return filmStorage.getPopularFilmsOrderByGenreYear(genreId, year, count);
        }
        if (year.isPresent()) {
            log.info("Вывод рейтинга фильмов по году");
            return filmStorage.getPopularFilmsOrderByYear(year, count);
        }
        if (genreId.isPresent()) {
            log.info("Вывод рейтинга фильмов по жанру");
            return filmStorage.getPopularFilmsOrderByGenre(genreId, count);
        }
        log.info("Вывод рейтинга фильмов по количеству лайков");
        return filmStorage.getPopularFilms(count);
    }
}
