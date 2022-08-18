package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendsDao;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class FriendsDaoImpl implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;

    private final UserDbStorage userDbStorage;

    public FriendsDaoImpl(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

//    Сохраняет и обновляет
    @Override
    public void saveFriend(long userId1, long userId2) {
        String sqlQuery = "MERGE INTO friends KEY (user1_id, user2_id, FS_STATUS_ID) VALUES (?, ?, ?)";
        // если второй пользователь добавил в друзья 1
        if (containsFriends(userId2, userId1)) {
            jdbcTemplate.update(sqlQuery, userId2, userId1, 1);
        } else {
            jdbcTemplate.update(sqlQuery, userId1, userId2, 2);
        }
        log.info("Пользователь {} в друзьях у пользователя {}", userId1, userId2);
    }

    private Boolean containsFriends(long userId, long friendId) {
        return jdbcTemplate.query("SELECT * FROM friends " +
                        "WHERE user1_id = ? AND user2_id = ?",
                ResultSet::next, userId, friendId);

    }

    @Override
    public List<User> getFriendsByUserId(long userId) {
        String sqlQuery =
                "SELECT user2_id AS main_user FROM friends\n" +
                        "WHERE user1_id = ? \n" +
                        "UNION\n" +
                        "SELECT user1_id\n" +
                        "FROM friends\n" +
                        "WHERE user2_id = ? AND fs_status_id = 1 ";
        List<Long> friends = jdbcTemplate.query(sqlQuery, this::mapRowToUserId, userId, userId);
        return friends.stream()
                .map(userDbStorage::getUserById)
                .collect(Collectors.toList());
    }

    private Long mapRowToUserId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("main_user");
    }


    @Override
    public void deleteFriends(long userId, long friendId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE USER1_ID = ? AND USER2_ID = ?";
        if (containsFriends(friendId, userId)) {
            if (getStatus(friendId, userId) == 1)
                saveFriend(userId, friendId);
            jdbcTemplate.update(sqlQuery, friendId, userId);
        } else {
            if (getStatus(userId, friendId) == 1)
                updateStatus(userId, friendId);
            jdbcTemplate.update(sqlQuery, userId, friendId);
        }
    }

    private void updateStatus(long userId1, long userId2) {
        String sqlQuery = "MERGE INTO friends KEY (user1_id, user2_id, FS_STATUS_ID) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId1, userId2, 2);
        log.info("Статус пользователя {} и пользователя {} изменен", userId1, userId2);
    }

    private Integer getStatus(long userId, long friendId) {
        String sqlQuery = "SELECT fs_status_id FROM friends " +
                "WHERE user1_id = ? AND user2_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToStatus, userId, friendId);
    }

    private int mapRowToStatus(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("fs_status_id");
    }
}
