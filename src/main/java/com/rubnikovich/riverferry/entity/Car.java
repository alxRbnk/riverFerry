package com.rubnikovich.riverferry.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Car implements Runnable {
    public static final Logger logger = LogManager.getLogger();
    private static Queue<Car> carsQueue;
    private int id;
    private int weight;
    private int area;
    private CarState carState;
    private Lock lock = new ReentrantLock();

    private enum CarState{
        QUEUE, ON_FERRY, UNLOADED
    }

    public Car(Queue<Car> carsQueue) {
        this.carsQueue = carsQueue;
    }

    public Car(int weight, int area, int id) {
        this.id = id;
        this.weight = weight;
        this.area = area;
        this.carState = CarState.QUEUE;
    }

    public int getCarId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public static Queue<Car> getCarsQueue() {
        return carsQueue;
    }

    public void changeState() {
            switch (carState) {
                case QUEUE -> carState = CarState.ON_FERRY;
                case ON_FERRY -> carState = CarState.UNLOADED;
                default -> carState = CarState.QUEUE;
            }
        }

    @Override
    public void run() {
        try {
            while (!carsQueue.isEmpty()) {
                Ferry.lock.lock();
                try {
                    loadCar();
                } finally {
                    Ferry.lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCar() throws InterruptedException {
        Ferry ferry = Ferry.getInstance();
        for (int i = 0; i < Ferry.LIMIT_COUNT; i++) {
            if (isInvalid()) {
                continue;
            }
            ferry.getCarsOnFerry().push(carsQueue.poll());
            ferry.getCarsOnFerry().peek().changeState();
            logger.info(ferry.getCarsOnFerry().peek());
            int countCarOnFerry = ferry.getCountCarsOnFerry();
            ferry.setCountCarsOnFerry(++countCarOnFerry);
            ferry.setSpaceOnFerry(ferry.getSpaceOnFerry() + ferry.getCarsOnFerry().peek().getArea());
            ferry.setWeightLoaded(ferry.getWeightLoaded() + ferry.getCarsOnFerry().peek().getWeight());
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    private boolean isInvalid() {
        Ferry ferry = Ferry.getInstance();
        return carsQueue.isEmpty() ||
                (ferry.getSpaceOnFerry() + carsQueue.peek().getArea()) > Ferry.LIMIT_AREA ||
                (ferry.getWeightLoaded() + carsQueue.peek().getWeight()) > Ferry.LIMIT_WEIGHT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (id != car.id) return false;
        if (weight != car.weight) return false;
        if (area != car.area) return false;
        return Objects.equals(carState, car.carState);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + weight;
        result = 31 * result + area;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("").append(id);
        sb.append(",").append(weight);
        sb.append(",").append(area);
        sb.append(",").append(carState);
        sb.append('}');
        return sb.toString();
    }
}