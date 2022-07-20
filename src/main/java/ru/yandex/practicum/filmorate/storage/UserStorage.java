package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsersList();

    void putUser(Long id, User user);

    Boolean containsUser(User user);

    Boolean containsUser(long id);

    User returnUserById(long id);

    int getSize();
}
