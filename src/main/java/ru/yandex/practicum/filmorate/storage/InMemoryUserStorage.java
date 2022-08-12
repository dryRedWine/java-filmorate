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

    private final List<String> status = List.of("На рассмотрении", "Подтверждено");

    @Override
    public List<User> get() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void put(Long id, User user) {
        users.put(user.getId(), user);
    }

    //    Для проверки сразу и id, и email (по методу equals)
    @Override
    public Boolean contains(User user) {
        return users.containsValue(user);
    }

    //    Данный метод используется уже после валидации, чтобы сделать код проще и искать только по id
    @Override
    public Boolean contains(long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public int getSize() {
        return users.size();
    }


}
