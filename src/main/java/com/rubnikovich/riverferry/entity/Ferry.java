package com.rubnikovich.riverferry.entity;

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
    private static final int TOTAL_CARS = Cars.getInstance().getCarsQueue().size();
    private static volatile Ferry instance;
    public static Lock lock = new ReentrantLock();
    private Deque<Car> carsOnFerry = new ArrayDeque<>();
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

    public Deque<Car> getCarsOnFerry() {
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

    public boolean isFull() {
        Cars cars = Cars.getInstance();
        return (spaceOnFerry + cars.getCarsQueue().peek().getArea()) > Ferry.LIMIT_AREA ||
                (weightLoaded + cars.getCarsQueue().peek().getWeight()) > Ferry.LIMIT_WEIGHT;
    }

    @Override
    public void run() {
        try {
            while (Cars.getInstance().getCarsLoaded().size() != TOTAL_CARS) {
                lock.lock();
                try {
                    unloadFerry();
                    TimeUnit.MILLISECONDS.sleep(1000);
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
            Cars.getInstance().getCarsLoaded().push(carsOnFerry.pop());
            Cars.getInstance().getCarsLoaded().peek().changeState();
            logger.info(Cars.getInstance().getCarsLoaded().peek());
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
