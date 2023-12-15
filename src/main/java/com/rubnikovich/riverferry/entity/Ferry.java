package com.rubnikovich.riverferry.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ferry implements Runnable {
    public static final Logger logger = LogManager.getLogger();
    public static final int LIMIT_COUNT = 5;
    public static final int LIMIT_AREA = 50;
    public static final int LIMIT_WEIGHT = 12_000;
    private static final int TOTAL_CARS = Car.getCarsQueue().size();
    private ArrayDeque<Car> carsOnFerry = new ArrayDeque<>();
    private ArrayDeque<Car> carsUnloaded = new ArrayDeque<>();
    private int spaceOnFerry;
    private int countCarsOnFerry;
    private int weightLoaded;
    public static Lock lock = new ReentrantLock();

    private static class InstanceFerry {
        private static final Ferry instance = new Ferry();
    }

    private Ferry() {
    }

    public static Ferry getInstance() {
        return InstanceFerry.instance;
    }

    public ArrayDeque<Car> getCarsOnFerry() {
        return carsOnFerry;
    }

    public int getCountCarsOnFerry() {
        return countCarsOnFerry;
    }

    public void setCountCarsOnFerry(int countCarsOnFerry) {
        this.countCarsOnFerry = countCarsOnFerry;
    }

    public int getSpaceOnFerry() {
        return spaceOnFerry;
    }

    public void setSpaceOnFerry(int spaceOnFerry) {
        this.spaceOnFerry = spaceOnFerry;
    }

    public int getWeightLoaded() {
        return weightLoaded;
    }

    public void setWeightLoaded(int weightLoaded) {
        this.weightLoaded = weightLoaded;
    }

    public ArrayDeque<Car> getCarsUnloaded() {
        return carsUnloaded;
    }

    @Override
    public void run() {
        try {
            while (carsUnloaded.size() != TOTAL_CARS) {
                TimeUnit.MILLISECONDS.sleep(5);
                try {
                    lock.lock();
                    logger.info(this);
                    unloadFerry();
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void unloadFerry() throws InterruptedException {
        for (int i = 0; i < LIMIT_COUNT; i++) {
            if (carsOnFerry.isEmpty()) {
                continue;
            }
            TimeUnit.MILLISECONDS.sleep(300);
            carsUnloaded.push(carsOnFerry.pop());
            carsUnloaded.peek().changeState();
            logger.info(carsUnloaded.peek());
            countCarsOnFerry--;
        }
        spaceOnFerry = 0;
        weightLoaded = 0;
        logger.info("Empty ferry, return beach");
        TimeUnit.SECONDS.sleep(1);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Ferry{");
        sb.append("spaceOnFerry=").append(spaceOnFerry);
        sb.append(", countCarsOnFerry=").append(countCarsOnFerry);
        sb.append(", weightLoaded=").append(weightLoaded);
        sb.append('}');
        return sb.toString();
    }
}
