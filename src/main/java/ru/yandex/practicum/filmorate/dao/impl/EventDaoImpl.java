package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.model.eventEnum.EventType;

import java.sql.*;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {

    private final JdbcTemplate jdbcTemplate;

  private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder().eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(EventOperation.valueOf(rs.getString("event_operation")))
                .timestamp(rs.getLong("event_timestamp"))
                .build();
    }

    public Event addEvent(long userId, EventType eventType, EventOperation eventOperation, long entityId) {
        return Event.builder().userId(userId)
                .eventType(eventType)
                .operation(eventOperation)
                .entityId(entityId)
                .build();
    }


    public Event saveEvent(Event event) {

        String sql = "insert into EVENTS (ENTITY_ID, EVENT_TYPE, EVENT_OPERATION, EVENT_TIMESTAMP, USER_ID)" +
                " values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"EVENT_ID"});
            stmt.setLong(1, event.getEntityId());
            stmt.setString(2, event.getEventType().toString());
            stmt.setString(3, event.getOperation().toString());
            stmt.setLong(4, event.getTimestamp());
            stmt.setLong(5, event.getUserId());
            return stmt;
        }, keyHolder);
        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Событие успешно добавлено в таблицу events");
        return event;
    }

    public List<Event> getEventUserById(long userId) {
        String sql = "SELECT * FROM EVENTS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToEvent, userId);
    }
}
