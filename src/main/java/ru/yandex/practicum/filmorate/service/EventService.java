package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.EventDaoImpl;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.model.eventEnum.EventType;

import java.util.List;

@Service
public class EventService {

    private final EventDaoImpl eventDaoImpl;

    public EventService(EventDaoImpl eventDaoImpl) {
        this.eventDaoImpl = eventDaoImpl;
    }

    public Event addEvent(long userId, EventType eventType, EventOperation eventOperation, long entityId) {
        return eventDaoImpl.addEvent(userId, eventType, eventOperation, entityId);
    }

    public Event saveEvent(Event event) {
        return eventDaoImpl.saveEvent(event);
    }

    public List<Event> getEventUserById(long userId) {
        return eventDaoImpl.getEventUserById(userId);
    }
}
