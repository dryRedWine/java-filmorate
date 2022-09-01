package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User getUserById(long id) {
        // выполняем запрос к базе данных.
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::makeUser, id);
    }
    private @Valid User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public User saveUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        log.info("Пользователь успешно сохранен в таблице users");
        return getUserById(userId);
    }

    @Override
    public User update(User user) {
        String sqlQuery =
                "MERGE INTO users KEY (id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday());
        return getUserById(user.getId());
    }

    @Override
    public Boolean contains(long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", ResultSet::next, id);

    }

    @Override
    public Boolean contains(User user) {
        return jdbcTemplate.query("SELECT * FROM users " +
                "WHERE NAME = ? AND LOGIN = ? AND EMAIL = ? AND BIRTHDAY = ?",
                ResultSet::next, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday());

    }


    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void deleteUser(long userId) {
        String sqlQuery = "DELETE FROM users WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, userId);
    }

}
