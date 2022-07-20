package ru.yandex.practicum.filmorate;

import java.util.*;

public class Test {
    public static void main( String  [] args ) {
        Set<String> listOne = new HashSet<>(Arrays.asList("milan","dingo", "elpha", "hafil", "meat", "iga", "neeta.peeta"));
        System.out.println(listOne);
        Set<String> testSet = listOne;
        System.out.println(testSet);
//        Set<String> listTwo = new HashSet<>(Arrays.asList("hafil", "iga", "binga", "mike", "dingo"));
//        listOne.retainAll( listTwo );
//        System.out.println( listOne );
    }
}
