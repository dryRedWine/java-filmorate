package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new LinkedHashMap<>();

    private final List<String> rating = List.of("G", "PG", "PG", "R", "NC");

    private final List<String> genre = List.of(
            "Комедия", "Драма", "Мультфильм",
            "Триллер", "Документальный", "Боевик");

    @Override
    public List<Film> getList() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void put(Long id, Film film) {
        films.put(id, film);
    }

    @Override
    public Boolean contains(Film film) {
        return films.containsValue(film);
    }

    @Override
    public Boolean contains(long id) {
        return films.containsKey(id);
    }

    @Override
    public Film getFilmById(long id) {
        return films.get(id);
    }

    @Override
    public int getSize() {
        return films.size();
    }
}
