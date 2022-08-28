package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.model.eventEnum.EventType;

import java.util.List;

public interface EventDao {
    Event addEvent(long userId, EventType eventType, EventOperation eventOperation, long entityId);

    Event saveEvent(Event event);

    List<Event> getEventUserById(long userId);
}
