package com.rubnikovich.riverferry2.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ferry implements Runnable {
    public static final Logger logger = LogManager.getLogger();
    public static final int LIMIT_COUNT = 5;
    public static final int LIMIT_AREA = 50;
    public static final int LIMIT_WEIGHT = 12_000;
    private static volatile Ferry instance;
    public static Lock lock = new ReentrantLock();
    public static int counter;
    private Deque<Car> carsOnFerry = new ArrayDeque<>();
    private Deque<Car> carsQueue = new ArrayDeque<>();
    private Deque<Car> carsLoaded = new ArrayDeque<>();
    private int spaceOnFerry;
    private int countCarsOnFerry;
    private int weightLoaded;

    private Ferry() {
    }

    public static Ferry getInstance() {
        if (instance == null) {
            synchronized (Ferry.class) {
                if (instance == null) {
                    instance = new Ferry();
                }
            }
        }
        return instance;
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

    public Deque<Car> getCarQueue() {
        return carsQueue;
    }

    public Deque<Car> getCarOnFerry() {
        return carsOnFerry;
    }

    public Deque<Car> getCarLoaded() {
        return carsLoaded;
    }

    public boolean isFull(Car car) {
        return (spaceOnFerry + car.getArea() > LIMIT_AREA) ||
                (countCarsOnFerry + 1 > LIMIT_COUNT) ||
                (weightLoaded + car.getWeight() > LIMIT_WEIGHT);
    }

    @Override
    public void run() {
        try {
            while (carsLoaded.size() != counter) {
                lock.lock();
                try {
                    unloadFerry();
                    TimeUnit.MILLISECONDS.sleep(500);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            logger.error(e);
            Thread.currentThread().interrupt();
        }
    }

    private void unloadFerry() {
        if (carsOnFerry.isEmpty()) {
            return;
        }
        logger.info(this + " sails to unload");
        for (int i = 0; i < LIMIT_COUNT; i++) {
            if (carsOnFerry.isEmpty()) {
                continue;
            }
            carsLoaded.offerLast(carsOnFerry.pollLast());
            carsLoaded.peekLast().changeState();
            logger.info(carsLoaded.peekLast());
            countCarsOnFerry--;
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                logger.error(e);
                Thread.currentThread().interrupt();
            }
        }
        spaceOnFerry = 0;
        weightLoaded = 0;
        logger.info(this + " is empty, go back");
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
