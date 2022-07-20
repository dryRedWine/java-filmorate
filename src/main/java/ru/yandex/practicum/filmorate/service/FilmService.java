package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j

public class FilmService {

    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.storage = inMemoryFilmStorage;
    }

    private static final LocalDate DATE = LocalDate.of(1895, 12, 28);
    private long filmId = 0L;

    public Film create(Film film)
            throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        if (film.getReleaseDate().isBefore(DATE))
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        if (!storage.containsFilm(film)) {
            film.setId(++filmId);
            log.info("Данный фильм добавлен");
            storage.putFilm(film.getId(), film);
        } else {
            log.error("Данный фильм уже добавлен");
            throw new AlreadyExistException("Данный фильм уже добавлен");
        }
        return film;
    }

    public Film update(Film film) throws NegativeIdException {
        CheckForId.idCheck(film.getId());
        if (film.getReleaseDate().isBefore(DATE))
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        if (!storage.containsFilm(film)) {
            if (film.getId() == null)
                film.setId(++filmId);
            log.info("Данный фильм добавлен");
            storage.putFilm(film.getId(), film);
        } else {
            log.info("Данные о фильме обновлены");
            storage.putFilm(film.getId(), film);
        }
        return film;
    }

    public List<Film> get() {
        log.info("Текущее количество добавленных фильмов: {}", storage.getSize());
        return storage.getFilmsList();
    }

    public Film getFilmById(Long id) throws NegativeIdException {
        CheckForId.idCheck(id);
        if (!storage.containsFilm(id)) {
            log.error("Данный пользователь не существует");
            throw new InvalidIdInPathException("Данный пользователь не существует");
        }
        log.info("Заданный пользователь успешно возвращен");
        return storage.returnFilmById(id);
    }

    public void putLikeToFilm(Long id, Long userId) throws NegativeIdException {
        CheckForId.idCheck(id, userId);
        storage.returnFilmById(id).addLike(userId);
        log.info("+1 лайк");
    }

    public void deleteLikeToFilm(Long id, Long userId) throws NegativeIdException {
        CheckForId.idCheck(id, userId);
        storage.returnFilmById(id).deleteLike(userId);
        log.info("-1 лайк");
    }

    public List<Film> getPopularFilms(Long count) throws NegativeIdException {
        CheckForId.idCheck(count);
        log.info("Вывод рейтинга фильмов по количеству лайков");
        return storage.getFilmsList().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f1, Film f2) {
        return -1 * Long.compare(f1.returnLikesCount(), f2.returnLikesCount()); // обратный порядок сортировки
    }
}
