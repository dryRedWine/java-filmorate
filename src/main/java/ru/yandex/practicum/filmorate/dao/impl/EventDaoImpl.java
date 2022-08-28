package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.model.eventEnum.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {

    private final JdbcTemplate jdbcTemplate;

  private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder().id(rs.getLong("eventId"))
                .userId(rs.getLong("userId"))
                .entityId(rs.getLong("entityId"))
                .eventType(EventType.valueOf(rs.getString("eventType")))
                .eventOperation(EventOperation.valueOf(rs.getString("eventOperation")))
                .build();
    }

    public Event addEvent(long userId, EventType eventType, EventOperation eventOperation, long entityId) {
        return Event.builder().userId(userId)
                .eventType(eventType)
                .eventOperation(eventOperation)
                .entityId(entityId)
                .build();
    }


    public Event saveEvent(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("eventId");
        long eventId = simpleJdbcInsert.executeAndReturnKey(event.toEvent()).longValue();
        log.info("Событие успешно добавлено в таблицу events");
        return event;
    }

    public List<Event> getEventUserById(long userId) {
        String sql = "SELECT ev.*, ue.user_id FROM EVENTS AS ev INNER JOIN user_events as ue WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToEvent, userId);
    }



}
