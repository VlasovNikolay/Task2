package ru.application.task2;

import java.util.Random;

public class Bug {
    private final float trackLength;
    private final float maxPossibleStep;
    private final Random random;
    private int racePosition;
    private float x;
    private float y;

    public Bug(Random random, float trackLength, float x, float y) {
        this.random = random;
        this.trackLength = trackLength;
        maxPossibleStep = 0.02f * trackLength;
        this.x = x;
        this.y = y;
    }

    public int getRacePosition() {
        return racePosition;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setRacePosition(int racePosition) {
        this.racePosition = racePosition;
    }

    public void step() {
        y += random.nextFloat() * maxPossibleStep;
        if (y > trackLength) {
            y = trackLength;
        }
    }
}
