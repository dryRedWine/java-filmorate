package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage inMemoryStorage) {
        this.storage = inMemoryStorage;
    }

    //    Нужно ли добавить в storage? Или оставить тут?
    private long userId = 0L;

    private static void additionalCheck(User user) throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new NotBurnYetException("Пользователь еще не родился :)");
        if (user.getName().isBlank()) { // проверка имени на пустоту
            user.setName(user.getLogin());
            log.debug("Имени присвоено значение логина");
        }
    }

    public User create(User user)
            throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        additionalCheck(user);
        if (!storage.containsUser(user)) {
            user.setId(++userId);
            log.info("Пользователь добавлен");
            storage.putUser(user.getId(), user);
        } else {
            log.error("Данный пользователь уже добавлен");
            throw new AlreadyExistException("Данный пользователь уже добавлен");
        }
        return user;
    }

    public User update(User user) throws NegativeIdException {
        CheckForId.idCheck(user.getId());
        additionalCheck(user);
        if (!storage.containsUser(user)) {
            if (user.getId() == null)
                user.setId(++userId);
            log.info("Пользователь добавлен");
            storage.putUser(user.getId(), user);
        } else {
            log.info("Данные о пользователе обновлены");
            storage.putUser(user.getId(), user);
        }
        return user;
    }

    public List<User> get() {
        log.info("Текущее количество пользователей: {}", storage.getSize());
        return storage.getUsersList();
    }

    public List<User> returnListOfFriends(Long id) throws NegativeIdException {
        CheckForId.idCheck(id);
        if (!storage.containsUser(id)) {
            log.error("Данный пользователь не существует");
            throw new InvalidIdInPathException("Данный пользователь не существует");
        }
        final Set<Long> friendsId = storage.returnUserById(id).getFriends();
        List<User> friendsList = new ArrayList<>();
        for (Long tempId : friendsId)
            friendsList.add(storage.returnUserById(tempId));
        log.info("Возвращен список друзей заданного пользователя");
        return friendsList;
    }

    public User getUserById(Long id) throws NegativeIdException {
        CheckForId.idCheck(id);
        if (storage.containsUser(id)) {
            log.info("Заданный пользователь успешно возвращен");
            return storage.returnUserById(id);
        } else {
            log.error("Данный пользователь не существует");
            throw new InvalidIdInPathException("Данный пользователь не существует");
        }
    }

    public void addNewFriendById(Long id, Long friendId) throws NegativeIdException {
        CheckForId.idCheck(id, friendId);
        if (storage.containsUser(id) && storage.containsUser(friendId)) {
            storage.returnUserById(id).addNewFriend(friendId);
            storage.returnUserById(friendId).addNewFriend(id);
            log.info("Пользователь успешно добавлен в друзья :)");
        } else {
            log.error("Передан несуществующий id");
            throw new InvalidIdInPathException("Передан несуществующий id");
        }
    }

    public void deleteFriendById(Long id, Long friendId) throws NegativeIdException {
        CheckForId.idCheck(id, friendId);
        if (storage.containsUser(id) && storage.containsUser(friendId)) {
            storage.returnUserById(id).deleteFriend(friendId);
            storage.returnUserById(friendId).deleteFriend(id);
            log.info("Пользователь успешно удален из друзей :(");
        } else {
            log.error("Передан несуществующий id");
            throw new InvalidIdInPathException("Передан несуществующий id");
        }
    }

    public List<User> getMutualFriendsList(Long id, Long otherId) throws NegativeIdException {
        CheckForId.idCheck(id, otherId);
        Set<Long> helpful1 = new HashSet<>(storage.returnUserById(id).getFriends()); // множество друзей пользователя id
        Set<Long> helpful2 = new HashSet<>(storage.returnUserById(otherId).getFriends()); // множество друзей пользователя otherId
        helpful1.retainAll(helpful2); // выводит boolean, а итоговое множество лежит в helpful1
        List<User> result = new ArrayList<>();
        for (Long tempId : helpful1)
            result.add(storage.returnUserById(tempId));
        log.info("Список общих друзей успешно возвращен");
        return result;
    }
}
