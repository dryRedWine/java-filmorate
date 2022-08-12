package ru.yandex.practicum.filmorate.utility;

import ru.yandex.practicum.filmorate.exceptions.NegativeIdException;

public class CheckForId {

    public static void idCheck(long id){
        if (id < 1)
            throw new NegativeIdException("id должен быть строго больше 0");
    }

    public static void idCheck(long id, long secondId){
        if (id < 1 || secondId < 1)
            throw new NegativeIdException("id должен быть строго больше 0");
    }
}
