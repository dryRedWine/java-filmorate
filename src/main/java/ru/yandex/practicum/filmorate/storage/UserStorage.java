package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> get();

    void put(Long id, User user);

    Boolean contains(User user);

    Boolean contains(long id);

    User getUserById(long id);

    int getSize();
}
