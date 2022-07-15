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
@RequestMapping("/users")
@Slf4j
public class UserController extends CommonController<User> {
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private int userId = 0;

    @Override
    @PostMapping
    public @Valid User create(@Valid @RequestBody User user)
            throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        additionalCheck(user);
        if (!users.containsValue(user)) {
            user.setId(++userId);
            log.info("Пользователь добавлен");
            users.put(user.getId(), user);
        } else {
            log.warn("Данный пользователь уже добавлен");
            throw new AlreadyExistException("Данный пользователь уже добавлен");
        }
        return user;
    }

    private void additionalCheck(User user) throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new NotBurnYetException("Пользователь еще не родился :)");
        if (user.getName().isBlank()) { // проверка имени на пустоту
            user.setName(user.getLogin());
            log.debug("Имени присвоено значение логина");
        }
    }

    @Override
    @PutMapping
    public @Valid User update(@Valid @RequestBody User user) {
        if (user.getId() < 1)
            throw new IllegalArgumentException("id не мб меньше 1");
        additionalCheck(user);
        if (!users.containsValue(user)) {
            if (user.getId() == null)
                user.setId(++userId);
            log.info("Пользователь добавлен");
            users.put(user.getId(), user);
        } else {
            log.info("Данные о пользователе обновлены");
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    @GetMapping
    public List<User> get() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

}
