package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsDao {

    void saveFriend(long userId1, long userId2);

    List<User> getFriendsByUserId(long userId);

    void deleteFriends(long userId, long friendId);

}
