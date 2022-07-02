package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.IllegalLoginException;
import ru.yandex.practicum.filmorate.exceptions.NotBurnYetException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends CommonController<Film> {

    private static final LocalDate DATE = LocalDate.of(1895, 12, 28);

    private final Set<Film> films = new HashSet<>();
    private int filmId = 0;

    @Override
    @PostMapping
    public Film create(@Valid @RequestBody Film film)
            throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        if (film.getReleaseDate().isBefore(DATE))
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        if (!films.contains(film)) {
            film.setId(++filmId);
            idCheckForPost(film);
            log.info("Данный фильм добавлен");
            films.add(film);
        } else {
            log.warn("Данный фильм уже добавлен");
            throw new AlreadyExistException("Данный фильм уже добавлен");
        }
        return film;
    }

    private void idCheckForPost(Film film) {
        for (Film temp : films) {
            if (film.getId().equals(temp.getId())) {
                film.setId(++filmId);
                idCheckForPost(film);
            }
        }
        log.debug("id успешно установлен");
    }

    private void idCheckForPut(Film film) {
        for (Film temp : films) {
            if (film.equals(temp) && !film.getId().equals(temp.getId())) {
                log.info("Неверно указан id");
                throw new IllegalArgumentException("Неверный id при обновлении");
            } else if (!film.equals(temp) && film.getId().equals(temp.getId())) {
                log.info("Этот id уже занят");
                throw new IllegalArgumentException("Неверный id при обновлении");
            }

        }
        log.info("id успешно обновлен!");
    }

    @Override
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(DATE))
            throw new IllegalArgumentException("Выбрана ложная дата релиза");
        if (!films.contains(film)) {
            if (film.getId() == null)
                film.setId(++filmId);
            idCheckForPut(film);
            log.info("Данный фильм добавлен");
            films.add(film);
        } else {
            log.info("Данные о фильме обновлены");
            films.add(film);
        }
        return film;
    }

    @Override
    @GetMapping
    public List<Film> get() {
        log.info("Текущее количество добавленных фильмов: {}", films.size());
        return sort(films);
    }

    private List<Film> sort(Set<Film> set) {
        List<Film> list = new ArrayList<>(set);
        for (int i = 0; i < list.size() - 1; i++) {
            int minIndex = i;
            int minIndexId = list.get(minIndex).getId();
            for (int j = i + 1; j < list.size(); j++) {
                int jId = list.get(j).getId();
                if (jId < minIndexId)
                    minIndex = j;
            }
            Film temp = list.get(minIndex);
            list.set(minIndex, list.get(i));
            list.set(i, temp);
        }
        return list;
    }
}
