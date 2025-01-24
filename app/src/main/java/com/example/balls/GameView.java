package com.example.balls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Random;

public class GameView extends View {
    Context context;
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint textPaint = new Paint();
    float TEXT_SIZE = 120;
    float paddleX, paddleY;
    float oldX, oldPaddleX;
    int seconds;
    long startTime, timeInMilliseconds, timeSwapBuff, updateTime = 0L;
    Bitmap ball, paddle;
    int dWidth, dHeight;
    int ballWidth, ballHeight;
    MediaPlayer mpHit;
    Random random;
    boolean gameOver = false;

    public GameView(Context context) {
        super(context);
        this.context = context;
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);
        handler = new Handler();
        runnable = this::invalidate;
//        mpHit = MediaPlayer.create(context, R.raw.hit);

        startTime = SystemClock.uptimeMillis();

        textPaint.setColor(Color.RED);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        random = new Random();
        paddleY = (float) (dHeight * 4) / 5;
        paddleX = (float) dWidth / 2 - (float) paddle.getWidth() / 2;
        ballWidth = ball.getWidth();
        ballHeight = ball.getHeight();
        BallSpawner.start();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        List<BallSpawner.Ball> allBalls = BallSpawner.getBalls();

        for (int i = 0; i < allBalls.size(); i++) {
            BallSpawner.Ball currBall = allBalls.get(i);

            float currX = currBall.getBallX() + currBall.getVelocity().getX();
            float currY = currBall.getBallY() + currBall.getVelocity().getY();

            if ((currX >= dWidth - ball.getWidth()) || currX <= 0) {
                currBall.getVelocity().setX(currBall.getVelocity().getX() * -1);
            }
            if (currY <= 0) {
                currBall.getVelocity().setY(currBall.getVelocity().getY() * -1);
            }
            if (currY > paddleY + paddle.getHeight()) {
                gameOver = true;
                launchGameOver();
            }

            if (((currX + ball.getWidth()) >= paddleX)
                    && (currX <= paddleX + paddle.getWidth())
                    && (currY + ball.getHeight() >= paddleY)
                    && (currY + ball.getHeight() < paddleY + paddle.getHeight())) {
                if (mpHit != null) {
                    mpHit.start();
                }
                currBall.getVelocity().setX(currBall.getVelocity().getX());
                currBall.getVelocity().setY((currBall.getVelocity().getY()) * -1);
            }

            canvas.drawBitmap(ball, currX, currY, null);

            currBall.setBallX(currX);
            currBall.setBallY(currY);
            allBalls.set(i, currBall);
        }

        canvas.drawBitmap(paddle, paddleX, paddleY, null);
        canvas.drawText(countTimer(), 20, TEXT_SIZE, textPaint);

        if (!gameOver) {
            handler.postDelayed(runnable, UPDATE_MILLIS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (touchY >= paddleY) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.getX();
                oldPaddleX = paddleX;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                float shift = oldX - touchX;
                float newPaddleX = oldPaddleX - shift;
                if (newPaddleX <= 0) {
                    paddleX = 0;
                } else if (newPaddleX >= dWidth - paddle.getWidth()) {
                    paddleX = dWidth - paddle.getWidth();
                } else {
                    paddleX = newPaddleX;
                }
            }
        }
        return true;
    }

    private String countTimer() {
        timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
        updateTime = timeSwapBuff + timeInMilliseconds;
        seconds = (int)(updateTime/1000);
        int centiSeconds = (int)((updateTime%1000)/10);
        return seconds+":"+centiSeconds;
    }

    private void launchGameOver() {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("points", seconds);
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}
