package com.rubnikovich.riverferry.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

public class Cars implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    private static volatile Cars instance;
    private Deque<Car> carsQueue = new ArrayDeque<>();
    private Deque<Car> carsLoaded = new ArrayDeque<>();

    private Cars() {
    }

    public static Cars getInstance() {
        if (instance == null) {
            synchronized (Cars.class) {
                if (instance == null) {
                    instance = new Cars();
                }
            }
        }
        return instance;
    }

    public Deque<Car> getCarsQueue() {
        return carsQueue;
    }

    public Deque<Car> getCarsLoaded() {
        return carsLoaded;
    }

    public void setCarsQueue(Deque<Car> carsQueue) {
        this.carsQueue = carsQueue;
    }

    @Override
    public void run() {
        try {
            while (!carsQueue.isEmpty()) {
                Ferry.lock.lock();
                try {
                    loadCar();
                    TimeUnit.MILLISECONDS.sleep(1000);
                } finally {
                    Ferry.lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            logger.info(e);
            Thread.currentThread().interrupt();
        }
    }

    private void loadCar() {
        Ferry ferry = Ferry.getInstance();
        if (ferry.isFull()) {
            return;
        }
        for (int i = 0; i < Ferry.LIMIT_COUNT; i++) {
            if (carsQueue.isEmpty() || ferry.isFull()) {
                continue;
            }
            ferry.getCarsOnFerry().push(carsQueue.poll());
            ferry.getCarsOnFerry().peek().changeState();
            logger.info(ferry.getCarsOnFerry().peek());
            ferry.setCountCarsOnFerry(ferry.getCountCarsOnFerry() + 1);
            ferry.setSpaceOnFerry(ferry.getSpaceOnFerry() + ferry.getCarsOnFerry().peek().getArea());
            ferry.setWeightLoaded(ferry.getWeightLoaded() + ferry.getCarsOnFerry().peek().getWeight());
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                logger.error(e);
                Thread.currentThread().interrupt();
            }
        }
        logger.info("waiting ferry");
    }
}
