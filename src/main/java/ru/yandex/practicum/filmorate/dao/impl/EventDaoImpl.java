package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.model.eventEnum.EventType;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class EventDaoImpl implements EventDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

  private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder().id(rs.getLong("eventId"))
                .userId(rs.getLong("userId"))
                .entityId(rs.getLong("entityId"))
                .eventType(EventType.valueOf(rs.getString("eventType")))
                .eventOperation(EventOperation.valueOf(rs.getString("operation")))
                .timestamp(rs.getLong("timestamp"))
                .build();
    }

    @Override
    public Event addEvent(long userId, EventType eventType, EventOperation eventOperation, long entityId) {

        Date date = new Date();

        Event event = Event.builder().userId(userId)
                .eventType(eventType)
                .eventOperation(eventOperation)
                .entityId(entityId)
                .timestamp(date.getTime())
                .build();

        saveEvent(event);

        return event;
    }

    @Override
    public Event saveEvent(Event event) {

        String sql = "insert into EVENTS (ENTITYID, EVENTTYPE, OPERATION, TIMESTAMP)" +
                " values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"EVENTID"});
            stmt.setLong(1, event.getEntityId());
            stmt.setString(2, EventType.valueOf());
            final LocalDate releaseDate = film.getReleaseDate();
            if (releaseDate == null) {
                stmt.setNull(3, Types.DATE);
            } else {
                stmt.setDate(3, java.sql.Date.valueOf(releaseDate));
            }
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getRating().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return event;
        /*SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("eventId");
        long eventId = simpleJdbcInsert.executeAndReturnKey(event.toEvent()).longValue();
        log.info("Событие успешно добавлено в таблицу events");
        return getEventById(eventId); */
    }

    @Override
    public List<Event> getEventUserById(long userId) {
        String sql = "SELECT ev.*, ue.user_id FROM EVENTS AS ev INNER JOIN user_events as ue WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToEvent, userId);
    }

    @Override
    public Event getEventById(long eventId) {
        String sql = "SELECT * FROM EVENTS AS ev INNER JOIN user_events as ue WHERE EVENT_ID = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToEvent, eventId);
    }



}
