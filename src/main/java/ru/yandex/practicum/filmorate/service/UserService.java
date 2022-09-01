package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.algorithms.slope_one.SlopeOne;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.EventDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.FriendsDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.model.eventEnum.EventType;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final FriendsDaoImpl friendsDao;
    private final LikesDao likesDao;
    private final SlopeOne slopeOne;

    private final EventDaoImpl eventDaoImpl;

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendsDaoImpl friendsDao,
                       LikesDao likesDao,
                       SlopeOne slopeOne,
                       FilmStorage filmStorage,
                       EventDaoImpl eventDaoImpl) {
        this.userStorage = userStorage;
        this.friendsDao = friendsDao;
        this.likesDao = likesDao;
        this.slopeOne = slopeOne;
        this.filmStorage = filmStorage;
        this.eventDaoImpl = eventDaoImpl;
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
        CheckForId.idCheckEquals(id, friendId);
        if (userStorage.contains(id) && userStorage.contains(friendId)) {
            friendsDao.saveFriend(id, friendId);
            log.info("Пользователь успешно добавлен в друзья :)");
            eventDaoImpl.addEvent(id, EventType.FRIEND, EventOperation.ADD, friendId);
        } else {
            log.warn("Передан несуществующий id");
            throw new InvalidIdInPathException("Передан несуществующий id");
        }
    }

    public void deleteFriendById(Long id, Long friendId) throws NegativeIdException {
        CheckForId.idCheckEquals(id, friendId);
        if (userStorage.contains(id) && userStorage.contains(friendId)) {
            friendsDao.deleteFriends(id, friendId);
            log.info("Пользователь успешно удален из друзей :(");
            eventDaoImpl.addEvent(id, EventType.FRIEND, EventOperation.REMOVE, friendId);
        } else {
            log.warn("Передан несуществующий id");
            throw new InvalidIdInPathException("Передан несуществующий id");
        }
    }

    public Set<User> getMutualFriendsList(Long id, Long otherId) throws NegativeIdException {
        CheckForId.idCheckEquals(id, otherId);
        Set<User> userSet1 = new HashSet<>(friendsDao.getFriendsByUserId(id));
        Set<User> userSet2 = new HashSet<>(friendsDao.getFriendsByUserId(otherId));
        userSet1.retainAll(userSet2);
        log.info("Список общих друзей успешно возвращен");
        return userSet1;
    }


    public Collection<Film> getRecommendations(Long userId) {
        if (!userStorage.contains(userId)) {
            throw new NotBurnYetException("Пользователя с таким id не существует!");
        }

        // Запрашиваю список всех пользователей из таблицы likes
        List<Long> userIdList = likesDao.findAllUserIdFromLikes();
        List<Long> requestLikes = new ArrayList<>();
        Map<Long, List<Long>> likes = new HashMap<>();
        // Запрашиваю список всех пролайканных фильмов и добавляю к соответствующему пользователю
        for (Long id : userIdList)
            likes.put(id, likesDao.findAllFilmIdFromLikes(id));

        // Удаляю пользователя, для которого составляются рекомендации
        if (likes.containsKey(userId)) {
            requestLikes = likes.get(userId);
            likes.remove(userId);
        }
        return slopeOne.getRecommendations(requestLikes, likes).stream()
                .map(filmStorage::getFilmById).collect(Collectors.toList());
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
