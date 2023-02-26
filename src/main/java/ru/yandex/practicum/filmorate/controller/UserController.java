package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.IllegalLoginException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectPathException;
import ru.yandex.practicum.filmorate.exceptions.NotBurnYetException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    private final EventService eventService;

    public UserController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @Valid User create(@Valid @RequestBody User user)
            throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        log.info("Create user");
        return userService.create(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public @Valid User update(@Valid @RequestBody User user) {
        log.info("Update user");
        return userService.update(user);
    }

    // Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addNewFriendById(@PathVariable(value = "id") Long id,
                                 @PathVariable(value = "friendId") Long friendId) {
        log.info("Put new friend with userId={} and friendId={}", id, friendId);
        userService.addNewFriendById(id, friendId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> findAll() {
        log.info("Get all users");
        return userService.findAll();
    }

    // Возвращает список друзей определенного пользователя
    @GetMapping("/{id}/friends")
    public Collection<User> returnListOfFriends(@PathVariable(value = "id", required = false) Long id) {
        log.info("Get friends of user id={}", id);
        return userService.returnListOfFriends(id);
    }

    // Возвращает пользователя по id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable(value = "id", required = false) Long id) {
        log.info("Get user id={}", id);
        return userService.getUserById(id);
    }

    // Список друзей, общих с другим пользователем
    @GetMapping("{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getMutualFriendsList(@PathVariable(value = "id") Long id,
                                                 @PathVariable(value = "otherId") Long friendId) {
        log.info("Get common friends with id={} and otherId={}", id, friendId);
        return userService.getMutualFriendsList(id, friendId);
    }

    // Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriendById(@PathVariable(value = "id") Long id,
                                 @PathVariable(value = "friendId") Long friendId) {
        log.info("Delete friend with userId={} and otherId={}", id, friendId);
        userService.deleteFriendById(id, friendId);
    }

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getRecommendations(@PathVariable(value = "id", required = false) Long userId) {
        if (userId == null)
            throw new IncorrectPathException("Переменная пути не была передана");
        log.info("Get recommendations userId={}", userId);
        return userService.getRecommendations(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable(value = "userId") Long id) {
        log.info("Delete user id={}", id);
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}/feed")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Event> getFeed(@PathVariable(value = "id") Long id) {
        log.info("Get feed userId={}", id);
        return eventService.getEventUserById(id);
    }


}
