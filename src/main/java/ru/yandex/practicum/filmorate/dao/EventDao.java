package ru.yandex.practicum.filmorate.dao;

public interface EventDao {
    Event addEvent(long userId, EventType eventType, EventOperation eventOperation, long entityId);

    Event saveEvent(Event event);

    List<Event> getEventUserById(long userId);
}
