package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.IllegalLoginException;
import ru.yandex.practicum.filmorate.exceptions.NotBurnYetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends CommonController<Film> {

    private static final LocalDate DATE = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private int filmId = 0;

    @Override
    @PostMapping
    public @Valid Film create(@Valid @RequestBody Film film)
            throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        if (film.getReleaseDate().isBefore(DATE))
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        if (!films.containsValue(film)) {
            film.setId(++filmId);
            log.info("Данный фильм добавлен");
            films.put(film.getId(), film);
        } else {
            log.warn("Данный фильм уже добавлен");
            throw new AlreadyExistException("Данный фильм уже добавлен");
        }
        return film;
    }

    @Override
    @PutMapping
    public @Valid Film update(@Valid @RequestBody Film film) {
        if (film.getId() < 1)
            throw new IllegalArgumentException("id не мб меньше 1");
        if (film.getReleaseDate().isBefore(DATE))
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        if (!films.containsValue(film)) {
            if (film.getId() == null)
                film.setId(++filmId);
            log.info("Данный фильм добавлен");
            films.put(film.getId(), film);
        } else {
            log.info("Данные о фильме обновлены");
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    @GetMapping
    public List<Film> get() {
        log.info("Текущее количество добавленных фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }
}
