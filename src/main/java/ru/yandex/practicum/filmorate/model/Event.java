package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import ru.yandex.practicum.filmorate.model.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.model.eventEnum.EventType;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
public class Event {
    private Long id;

    private Long userId;

    private Long entityId;

    private EventType eventType;

    private EventOperation eventOperation;

    private long timestamp;

    public Map<String, Object> toEvent() {
        Map<String, Object> values = new HashMap<>();
        values.put("userId", userId);
        values.put("entityId", entityId);
        values.put("eventType", eventType);
        values.put("eventOperation", eventOperation);
        return values;
    }

}
