package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

public abstract class CommonController<T> {

    abstract T create(@Valid @RequestBody T element);

    abstract T update(@Valid @RequestBody T element);

    abstract List<T> get();
}
