package ru.yandex.practicum.filmorate.service;

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
