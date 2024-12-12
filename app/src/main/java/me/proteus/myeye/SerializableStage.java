package me.proteus.myeye;

import java.io.Serializable;

public class SerializableStage implements Serializable {
    private final String first;
    private final String second;
    private final int difficulty;

    public SerializableStage(String first, String second, int difficulty) {
        this.first = first;
        this.second = second;
        this.difficulty = difficulty;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public int getDifficulty() {
        return difficulty;
    }
}