package com.rubnikovich.riverferry.entity;

import com.rubnikovich.riverferry.lock.CustomLock;
import com.rubnikovich.riverferry.state.StateFerry;

import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ferry extends Thread {
    public static final int LIMIT = 5;
    public static int countCars = 0;
    public static Stack<Car> carsOnFerry = new Stack<>();
    public static Lock lock = new ReentrantLock();
    private StateFerry stateFerry;

    public Ferry() {
    }

    public void unload() throws InterruptedException {
        Thread.sleep(50);
        while (Cars.carsQueue.size() != 0) {
            CustomLock.lock.lock();
            Thread.sleep(1000);
            System.out.println("Ferry lock");
            try {
                unloadFerry();
            } finally {
                Thread.sleep(2000);
                CustomLock.lock.unlock();
                System.out.println("Ferry unlock");
            }
        }
    }


    public void unloadFerry() throws InterruptedException {
        for (int i = 0; i < countCars; i++) {
            if (carsOnFerry.size() == 0) {
                continue;
            }
            Cars.carsUnloaded.push(carsOnFerry.pop());
            Cars.carsUnloaded.peek().changeState();
            System.out.println(Cars.carsUnloaded.peek());
            Thread.sleep(500);
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
