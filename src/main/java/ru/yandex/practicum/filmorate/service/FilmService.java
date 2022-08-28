package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.model.eventEnum.EventType;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final LikesDao likesDao;

    private final EventService eventService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage inMemoryFilmStorage, LikesDao likesDao, EventService eventService) {
        this.filmStorage = inMemoryFilmStorage;
        this.likesDao = likesDao;
        this.eventService = eventService;
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
        return filmStorage.findAll();
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
        likesDao.putLike(film_id, favId);
        log.info("+1 лайк");
        eventService.addEvent(favId, EventType.LIKE, EventOperation.ADD, film_id);
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
}
