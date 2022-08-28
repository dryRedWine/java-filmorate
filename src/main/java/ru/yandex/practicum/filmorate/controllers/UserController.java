package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.IllegalLoginException;
import ru.yandex.practicum.filmorate.exceptions.NotBurnYetException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
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
        return userService.create(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public @Valid User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    // Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addNewFriendById(@PathVariable(value = "id") Long id,
                                 @PathVariable(value = "friendId") Long friendId) {
        userService.addNewFriendById(id, friendId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> findAll() {
        return userService.findAll();
    }

    // Возвращает список друзей определенного пользователя
    @GetMapping("/{id}/friends")
    public Collection<User> returnListOfFriends(@PathVariable(value = "id", required = false) Long id) {
        return userService.returnListOfFriends(id);
    }

    // Возвращает пользователя по id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable(value = "id", required = false) Long id) {
        return userService.getUserById(id);

    }

    // Список друзей, общих с другим пользователем
    @GetMapping("{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getMutualFriendsList(@PathVariable(value = "id") Long id,
                                                 @PathVariable(value = "otherId") Long friendId) {
        return userService.getMutualFriendsList(id, friendId);
    }

    // Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriendById(@PathVariable(value = "id") Long id,
                                   @PathVariable(value = "friendId") Long friendId) {
        userService.deleteFriendById(id, friendId);
    }

    @GetMapping("/{id}/feed")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Event> getFeed(@PathVariable(value = "id") Long id) {
        return eventService.getEventUserById(id);
    }

}
