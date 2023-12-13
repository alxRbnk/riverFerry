package com.rubnikovich.riverferry.entity;

import com.rubnikovich.riverferry.util.CustomLock;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class Ferry implements Runnable {
    public static final int LIMIT = 5;
    public static int countCars = 0;
    public static Stack<Car> carsOnFerry = new Stack<>();
    private int countCarsNeed = Cars.carsQueue.size();

    private static class CustomSingleton {
        private static final Ferry instance = new Ferry();
    }

    private Ferry() {
    }

    public static Ferry getInstance() {
        return CustomSingleton.instance;
    }

    private void unload() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(50);
        while (Cars.carsUnloaded.size() != countCarsNeed) {
            CustomLock.lock.lock();
            System.out.println("Ferry lock");
            System.out.println("count car on ferry " + Ferry.carsOnFerry.size());
            try {
                unloadFerry();
            } finally {
                CustomLock.lock.unlock();
                System.out.println("Ferry unlock");
                TimeUnit.MILLISECONDS.sleep(200);
            }
        }
    }

    private void unloadFerry() throws InterruptedException {
        for (int i = 0; i < countCars; i++) {
            if (carsOnFerry.size() == 0) {
                continue;
            }
            Cars.carsUnloaded.push(carsOnFerry.pop());
            Cars.carsUnloaded.peek().changeState();
            System.out.println(Cars.carsUnloaded.peek());
        }
    }

    @Override
    public void run() {
        try {
            unload();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getCountCars() {
        return carsOnFerry.size();
    }
}
