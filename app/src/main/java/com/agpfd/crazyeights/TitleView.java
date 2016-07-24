package com.agpfd.crazyeights;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.MotionEvent;
import android.content.Context;
import android.graphics.Canvas;

/**
 * @author <a mailto="jacobibanez@jacobibanez.com">Jacob Ibáñez Sánchez</a>
 * @since 22/07/2016
 */
public class TitleView extends View {

    private final double ICON_TOP_MARGIN = 0.05;
    private final double PLAY_BUTTON_BOTTOM_MARGIN = 0.7;

    private Bitmap titleGraphic;
    private Bitmap playButtonUp;
    private Bitmap playButtonDown;
    private boolean playButtonPressed;
    private Context myContext;
    private int screenW;
    private int screenH;

    public TitleView(Context context) {
        super(context);
        myContext = context;
        titleGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.title_graphic);
        playButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_up);
        playButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_down);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenH = h;
        screenW = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(titleGraphic, (screenW - titleGraphic.getWidth()) / 2, (int) (screenH * ICON_TOP_MARGIN), null);
        if (playButtonPressed) {
            canvas.drawBitmap(playButtonDown, (screenW - playButtonDown.getWidth()) / 2, (int) (screenH * PLAY_BUTTON_BOTTOM_MARGIN), null);
        } else {
            canvas.drawBitmap(playButtonUp, (screenW - playButtonUp.getWidth()) / 2, (int) (screenH * PLAY_BUTTON_BOTTOM_MARGIN), null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();
        int X = (int) event.getX();
        int Y = (int) event.getY();
        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                if (X > (screenW - playButtonUp.getWidth()) / 2
                        && X < ((screenW - playButtonUp.getWidth()) / 2) + playButtonUp.getWidth()
                        && Y > screenH * PLAY_BUTTON_BOTTOM_MARGIN && Y < screenH * PLAY_BUTTON_BOTTOM_MARGIN + playButtonUp.getHeight()) {
                    playButtonPressed = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (playButtonPressed) {
                    Intent gameIntent = new Intent(myContext, GameActivity.class);
                    myContext.startActivity(gameIntent);
                }
                playButtonPressed = false;
                break;
        }
        invalidate();
        return true;
    }
}
