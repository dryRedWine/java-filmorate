package ru.yandex.practicum.filmorate.utility;

public enum EventType {
    LIKE("LIKE"),
    DISLIKE("DISLIKE");

    private final String name;

    private EventType(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}
