package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director get(long id) {
        CheckForId.idCheck(id);
        String sqlQuery = "SELECT * FROM DIRECTORS WHERE id = ?";
        log.info("Получен режиссер из базы данных id={}", id);
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
    }

    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT * FROM DIRECTORS";
        List<Director> directors = jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
        log.debug("Получены режиссеры из базы данных {}", directors);
        return directors;
    }

    @Override
    public Director create(Director director) {
        String sqlQuery = "INSERT INTO DIRECTORS(NAME) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"DIRECTOR_ID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Режиссер {} записан в базу данных", director);
        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE DIRECTORS SET NAME = ? WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        log.info("данные режиссера {} обновлены в базе данных", director);
        return get(director.getId());
    }

    @Override
    public void delete(Director director) {
        String sqlQuery = "DELETE FROM DIRECTORS WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, director.getId());
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }

    public boolean contains(long id) {
        return Boolean.TRUE.equals(jdbcTemplate.query("SELECT * FROM DIRECTORS WHERE id = ?", ResultSet::next, id));

    }
}
