package com.example.playmusic_group.shared.util;

import kotlin.jvm.functions.Function1;

public class ListHelper {
    public static <T> int indexOfFirst(Iterable<T> iterable, Function1<T, Boolean> predicate) {
        int index = 0;
        for (T item : iterable) {
            if (predicate.invoke(item)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
