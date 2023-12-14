package com.rubnikovich.riverferry.entity;

import com.rubnikovich.riverferry.state.CarState;
import com.rubnikovich.riverferry.state.impl.CarOnFerry;
import com.rubnikovich.riverferry.state.impl.CarQueue;
import com.rubnikovich.riverferry.state.impl.CarUnloaded;
import com.rubnikovich.riverferry.util.CustomLock;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Car implements Runnable {
    private static Queue<Car> carsQueue;
    private int id;
    private int weight;
    private int area;
    private CarState carState;

    public Car(Queue<Car> carsQueue) {
        this.carsQueue = carsQueue;
    }

    public Car(int weight, int area, int id) {
        this.id = id;
        this.weight = weight;
        this.area = area;
        this.carState = new CarQueue();
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
        Ferry ferry = Ferry.getInstance();
        if (ferry.getCarsOnFerry().contains(this)) {
            carState = new CarOnFerry();
        } else if (ferry.getCarsUnloaded().contains(this)) {
            carState = new CarUnloaded();
        } else if (carsQueue.contains(this)) {
            carState = new CarQueue();
        }
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName("nameThread = carThread");
            while (!carsQueue.isEmpty()) {
                CustomLock.lock.lock();
                Ferry.logger.info("LOCK " + Thread.currentThread().getName());
                try {
                    loadCar();
                } finally {
                    Ferry.logger.info("UNLOCK");
                    CustomLock.lock.unlock();
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCar() {
        Ferry ferry = Ferry.getInstance();
        for (int i = 0; i < Ferry.LIMIT_COUNT; i++) {
            if (isInvalid()) {
                continue;
            }
            ferry.getCarsOnFerry().push(carsQueue.poll());
            ferry.getCarsOnFerry().peek().changeState();
            Ferry.logger.info(ferry.getCarsOnFerry().peek());
            int countCarOnFerry = ferry.getCountCarsOnFerry();
            ferry.setCountCarsOnFerry(++countCarOnFerry);
            ferry.setSpaceOnFerry(ferry.getSpaceOnFerry() + ferry.getCarsOnFerry().peek().getArea());
            ferry.setWeightLoaded(ferry.getWeightLoaded() + ferry.getCarsOnFerry().peek().getWeight());
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
        sb.append(",").append(this.carState.getState());
        sb.append('}');
        return sb.toString();
    }
}