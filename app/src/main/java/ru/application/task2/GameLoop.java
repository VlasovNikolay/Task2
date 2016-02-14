package ru.application.task2;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.util.Random;

public class GameLoop implements Runnable {
    private final int numBugs = 4;
    private final long simulationTimeStepMs = 20;
    private final long frameTimeMs = 20;
    private final long maxAllowedSkips = 10;

    private int currentState;
    private final int STATE_ACTION = 2;
    private final int STATE_INITIALIZE = 1;

    private boolean running;
    private final int screenSizeX;
    private final int screenSizeY;
    private final int trackLength;
    private final Bitmap bugBitmap;
    private final Bug bugs[];
    private final Paint paint;
    private final SurfaceHolder surfaceHolder;

    public GameLoop(SurfaceHolder surfaceHolder, Resources resources, Context context, int state) {
        this.surfaceHolder = surfaceHolder;
        currentState = state;

        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, R.drawable.bug, options);

        screenSizeX = getScreenSize(context)[0];
        screenSizeY = getScreenSize(context)[1];
        int freeSpaceVar = (screenSizeX - options.outWidth * numBugs) / (numBugs + 1);
        trackLength = screenSizeY - options.outHeight - 100;

        bugs = new Bug[numBugs];
        Random random = new Random();
        for (int i = 1; i < numBugs + 1; i++) {
            bugs[i - 1] = new Bug(random, trackLength, freeSpaceVar * i + options.outWidth * (i - 1), 0);
        }

        paint = new Paint();
        paint.setTextSize(22);
        paint.setTextAlign(Paint.Align.CENTER);

        options.inScaled = false;
        options.inJustDecodeBounds = false;
        bugBitmap = BitmapFactory.decodeResource(resources, R.drawable.bug, options);
    }

    public void run() {
        long simulationTime = 0;
        long frameStartTime  = simulationTime;
        while (running) {
            switch (currentState) {
                case STATE_INITIALIZE:
                    drawGraphicsOnInitialize();
                    break;
                case STATE_ACTION:
                    if (simulationTime == 0) simulationTime = System.currentTimeMillis();
                    simulationTime = doPhysics(simulationTime, frameStartTime);
                    drawGraphics();
                    frameStartTime = waitForNextFrame(frameStartTime);
                    break;
            }
        }
    }

    private void drawGraphicsOnInitialize() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
            return;
        }
        try {
            synchronized (surfaceHolder) {
                canvas.drawColor(Color.WHITE);
                canvas.drawLine(0, trackLength, screenSizeX, trackLength, paint);
                for (Bug bug : bugs) {
                    canvas.drawBitmap(bugBitmap, bug.getX(), bug.getY(), paint);
                }
                canvas.drawText("Tap to start", screenSizeX / 2, screenSizeY / 2, paint);
            }
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private long doPhysics(long simulationTime, long frameStartTime) {
        for (int skipped = 0; skipped < maxAllowedSkips; skipped++) {
            if (simulationTime >= frameStartTime) break;

            for (Bug bug: bugs) {
                bug.step();
            }

            for (int i = 0; i < numBugs; i++) {

                if (bugs[i].getY() >= trackLength) break;

                int bugRaceNum = numBugs;
                for (int j = 0; j < numBugs; j++) {
                    if (i == j) continue;

                    if (bugs[i].getY() > bugs[j].getY()) bugRaceNum = bugRaceNum - 1;
                }
                bugs[i].setRacePosition(bugRaceNum);
            }
            simulationTime += simulationTimeStepMs;
        }
        return simulationTime;
    }

    private void drawGraphics() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
            return;
        }
        try {
            synchronized (surfaceHolder) {
                canvas.drawColor(Color.WHITE);
                canvas.drawLine(0, trackLength, screenSizeX, trackLength, paint);
                for (Bug bug : bugs) {
                    canvas.drawBitmap(bugBitmap, bug.getX(), bug.getY(), paint);
                    canvas.drawText(String.valueOf(bug.getRacePosition()), bug.getX() + 30, trackLength + 100, paint);
                }
            }
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private long waitForNextFrame(long frameStartTime) {
        long nextFrameStartTime = System.currentTimeMillis();
        long timeToWait = frameTimeMs - (nextFrameStartTime - frameStartTime);
        if (timeToWait > 0) {
            try {
                Thread.sleep(timeToWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return nextFrameStartTime;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int state){
        currentState = state;
    }

    public void setRunning(boolean flag) {
        running = flag;
    }

    private int [] getScreenSize(Context context){
        int [] scrSize = new int[2];
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError e) {
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        scrSize[0] = point.x;
        scrSize[1] = point.y;
        return scrSize;
    }
}
