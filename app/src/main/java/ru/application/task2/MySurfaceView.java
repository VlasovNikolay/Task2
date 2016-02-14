package ru.application.task2;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class MySurfaceView extends SurfaceView implements android.view.SurfaceHolder.Callback {
    private final int STATE_ACTION = 2;
    private final int STATE_INITIALIZE = 1;
    private final Context context;
    private final Resources resources;
    private Game game;

    public MySurfaceView(Context context, Resources resources) {
        super(context);
        this.resources = resources;
        this.context = context;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceholder) {
        game = new Game(surfaceholder, resources, context, STATE_INITIALIZE);
        game.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        game.stop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (game.getCurrentState() == STATE_INITIALIZE) {
            if (event.getAction() == MotionEvent.ACTION_UP) game.setCurrentState(STATE_ACTION);
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }
}
