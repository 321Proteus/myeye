package me.proteus.myeye;

import java.io.Serializable;

public class SerializablePair implements Serializable {
    private final String first;
    private final String second;

    public SerializablePair(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

}