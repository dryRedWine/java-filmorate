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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends CommonController<User> {

    private final Set<User> users = new HashSet<>();
    private int userId = 0;

    @Override
    @PostMapping
    public User create(@Valid @RequestBody User user)
            throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        AdditionalCheck(user);
        if (!users.contains(user)) {
            user.setId(++userId);
            idCheckForPost(user);
            log.info("Пользователь добавлен");
            users.add(user);
        } else {
            log.warn("Данный пользователь уже добавлен");
            throw new AlreadyExistException("Данный пользователь уже добавлен");
        }
        return user;
    }

    private void AdditionalCheck(User user) throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new NotBurnYetException("Пользователь еще не родился :)");
        if (user.getLogin().contains(" "))
            throw new IllegalLoginException("В логине не может содержаться пробел!");
        if (user.getName().isBlank()) { // проверка имени на пустоту
            user.setName(user.getLogin());
            log.debug("Имени присвоено значение логина");
        }
    }

    // проверка на повторение id
    // решил использовать set, поэтому придумал такой способ проверки, пока на небольших данный работает хорошо
    private void idCheckForPost(User user) {
        for (User temp : users) {
            if (user.getId().equals(temp.getId())) {
                user.setId(++userId);
                idCheckForPost(user);
            }
        }
        log.debug("id успешно установлен");
    }

    private void idCheckForPut(User user) {
        for (User temp : users) {
            if (user.equals(temp) && !user.getId().equals(temp.getId())) {
                log.info("Неверно указан id");
                throw new IllegalArgumentException("Неверный id при обновлении");
            } else if (!user.equals(temp) && user.getId().equals(temp.getId())) {
                log.info("Этот id уже занят");
                throw new IllegalArgumentException("Неверный id при обновлении");
            }

        }
        log.info("id успешно обновлен!");
    }

    @Override
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        AdditionalCheck(user);
        if (!users.contains(user)) {
            if (user.getId() == null)
                user.setId(++userId);
            idCheckForPut(user);
            log.info("Пользователь добавлен");
            users.add(user);
        } else {
            idCheckForPut(user);
            log.info("Данные о пользователе обновлены");
            users.add(user);
        }
        return user;
    }

    @Override
    @GetMapping
    public List<User> get() {
        log.info("Текущее количество пользователей: {}", users.size());
        return sort(users);
    }


    private List<User> sort(Set<User> set) {
        List<User> list = new ArrayList<>(set);
        for (int i = 0; i < list.size() - 1; i++) {
            int minIndex = i;
            int minIndexId = list.get(minIndex).getId();
            for (int j = i + 1; j < list.size(); j++) {
                int jId = list.get(j).getId();
                if (jId < minIndexId)
                    minIndex = j;
            }
            User temp = list.get(minIndex);
            list.set(minIndex, list.get(i));
            list.set(i, temp);
        }
        return list;
    }
}
