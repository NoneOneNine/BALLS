package com.example.balls;

import java.util.Random;

public class Velocity {
    private int x, y;
    Random random;

    public Velocity() {
        random = new Random();
        this.x = random.nextInt(27-22+1)+22;
        this.y = random.nextInt(35-30+1)+30;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
