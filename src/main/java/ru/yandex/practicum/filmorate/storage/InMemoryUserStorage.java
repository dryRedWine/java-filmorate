package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new LinkedHashMap<>();

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void putUser(Long id, User user) {
        users.put(user.getId(), user);
    }

    //    Для проверки сразу и id, и email (по методу equals)
    @Override
    public Boolean containsUser(User user) {
        return users.containsValue(user);
    }

    //    Данный метод используется уже после валидации, чтобы сделать код проще и искать только по id
    @Override
    public Boolean containsUser(long id) {
        return users.containsKey(id);
    }

    @Override
    public User returnUserById(long id) {
        return users.get(id);
    }

    @Override
    public int getSize() {
        return users.size();
    }


}
