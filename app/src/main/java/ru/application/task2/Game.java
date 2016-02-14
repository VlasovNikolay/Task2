package ru.application.task2;

import android.content.Context;
import android.content.res.Resources;
import android.view.SurfaceHolder;

public class Game {
    private final Thread thread;
    private GameLoop gameLoop;

    public Game(SurfaceHolder surfaceholder, Resources resources, Context context, int state) {
        gameLoop = new GameLoop(surfaceholder, resources, context, state);
        thread = new Thread(gameLoop);
    }

    public int getCurrentState() {
        return gameLoop.getCurrentState();
    }

    public void setCurrentState(int state) {
        gameLoop.setCurrentState(state);
    }

    public void start() {
        gameLoop.setRunning(true);
        thread.start();
    }

    public void stop() {
        boolean retry = true;
        gameLoop.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
