package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User saveUser(User user);

    Boolean contains(long id);

    Boolean contains(User user);

    User getUserById(long id);

    int getSize();

    User update(User user);

    void deleteUser(long userId);
}
