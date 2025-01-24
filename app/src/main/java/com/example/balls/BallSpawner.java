package com.example.balls;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

public class BallSpawner {
    // Ball class
    public static class Ball {
        private final long creationTime;
        private float ballX, ballY;
        private Velocity velocity;

        public Ball() {
            this.creationTime = System.currentTimeMillis();
            this.ballX = 0;
            this.ballY = 0;
            this.velocity = new Velocity();
        }

        public long getCreationTime() {
            return creationTime;
        }

        public float getBallX () {
            return ballX;
        }

        public void setBallX (float x) {
            this.ballX = x;
        }

        public float getBallY () {
            return ballY;
        }

        public void setBallY (float y) {
            this.ballY = y;
        }

        public Velocity getVelocity() {
            return this.velocity;
        }

        public void setVelocity(Velocity velocity) {
            this.velocity = velocity;
        }
    }

    private static List<Ball> balls = new ArrayList<>();
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final Runnable ballTask = new Runnable() {
        @Override
        public void run() {
            synchronized (balls) {
                Ball newBall = new Ball();
                balls.add(newBall);
                System.out.println("Ball created at: " + newBall.getCreationTime());
            }
            handler.postDelayed(this, 10_000); // Schedule task again in 10 seconds
        }
    };

    // Start creating balls
    public static void start() {
        handler.post(ballTask);
    }

    // Stop creating balls
    public static void stop() {
        handler.removeCallbacks(ballTask);
    }

    // Reset the list of balls
    public static void reset() {
//        stop(); // Stop creating balls
        synchronized (balls) {
            balls = new ArrayList<>(); // Replace the current list with a new empty list
        }
        System.out.println("Ball list has been reset.");
//        start(); // Optionally restart the task
    }

    // Get a copy of the list of balls
    public static List<Ball> getBalls() {
        synchronized (balls) {
            return new ArrayList<>(balls);
        }
    }
}
