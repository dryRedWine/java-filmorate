package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.FriendsDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final FriendsDaoImpl friendsDao;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendsDaoImpl friendsDao) {
        this.userStorage = userStorage;
        this.friendsDao = friendsDao;
    }

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
        if (!userStorage.contains(user)) {
            log.info("Пользователь добавлен");
            return userStorage.saveUser(user);
        } else {
            log.warn("Данный пользователь уже добавлен");
            throw new AlreadyExistException("Данный пользователь уже добавлен");
        }
    }

    public User update(User user) throws NegativeIdException {
        CheckForId.idCheck(user.getId());
        additionalCheck(user);
        userStorage.update(user);
        log.info("Данные о пользователе добавлены или обновлены");
        return user;
    }

    public List<User> findAll() {
//        log.info("Текущее количество пользователей: {}", userStorage.getSize());
        return userStorage.findAll();
    }

    public List<User> returnListOfFriends(long id) throws NegativeIdException {
        CheckForId.idCheck(id);
        if (!userStorage.contains(id)) {
            log.warn("Данный пользователь не существует");
            throw new InvalidIdInPathException("Данный пользователь не существует");
        }
        List<User> friends = friendsDao.getFriendsByUserId(id);
        log.info("Возвращен список друзей заданного пользователя");
        return friends;
    }

    public User getUserById(long id) throws NegativeIdException {
        CheckForId.idCheck(id);
        if (userStorage.contains(id)) {
            log.info("Заданный пользователь успешно возвращен");
            return userStorage.getUserById(id);
        } else {
            log.warn("Данный пользователь не существует");
            throw new InvalidIdInPathException("Данный пользователь не существует");
        }
    }

    public void addNewFriendById(long id, long friendId) throws NegativeIdException {
        CheckForId.idCheck(id, friendId);
        if (userStorage.contains(id) && userStorage.contains(friendId)) {
            friendsDao.saveFriend(id, friendId);
            log.info("Пользователь успешно добавлен в друзья :)");
        } else {
            log.warn("Передан несуществующий id");
            throw new InvalidIdInPathException("Передан несуществующий id");
        }
    }

    public void deleteFriendById(Long id, Long friendId) throws NegativeIdException {
        CheckForId.idCheck(id, friendId);
        if (userStorage.contains(id) && userStorage.contains(friendId)) {
            friendsDao.deleteFriends(id, friendId);
            log.info("Пользователь успешно удален из друзей :(");
        } else {
            log.warn("Передан несуществующий id");
            throw new InvalidIdInPathException("Передан несуществующий id");
        }
    }

    public Set<User> getMutualFriendsList(Long id, Long otherId) throws NegativeIdException {
        CheckForId.idCheck(id, otherId);
        Set<User> userSet1 = new HashSet<>(friendsDao.getFriendsByUserId(id));
        Set<User> userSet2 = new HashSet<>(friendsDao.getFriendsByUserId(otherId));
        userSet1.retainAll(userSet2);
        log.info("Список общих друзей успешно возвращен");
        return userSet1;
    }

    public void deleteUserById(Long id) throws NegativeIdException {
        //CheckForId.idCheck(id);
        if (userStorage.contains(id)) {
            userStorage.deleteUser(id);
            log.info("Пользователь успешно удален :(");
        } else {
            log.warn("Передан несуществующий id");
            throw new InvalidIdInPathException("Передан несуществующий id");
        }
    }
}
