package com.rubnikovich.riverferry2.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Car implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    private static int counter;
    private int id;
    private int weight;
    private int area;
    private CarState carState;

    private enum CarState {
        NEW, QUEUE, ON_FERRY, UNLOADED;
    }

    public Car(int weight, int area, int id) {
        this.id = id;
        this.weight = weight;
        this.area = area;
        this.carState = CarState.NEW;
        counter++;
    }

    public void changeState() {
        Ferry ferry = Ferry.getInstance();
        if (ferry.getCarQueue().contains(this)) {
            this.setCarState(CarState.QUEUE);
        } else if (ferry.getCarOnFerry().contains(this)) {
            this.setCarState(CarState.ON_FERRY);
        } else if (ferry.getCarLoaded().contains(this)) {
            this.setCarState(CarState.UNLOADED);
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName(String.valueOf(this.getId()));
        while (this.carState != CarState.ON_FERRY) {
            Ferry.lock.lock();
            try {
                try {
                    load();
                    TimeUnit.MILLISECONDS.sleep(300);
                } finally {
                    Ferry.lock.unlock();
                }
            } catch (InterruptedException e) {
                logger.info(e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void load() {
        Ferry ferry = Ferry.getInstance();
        if (ferry.isFull(this)) {
            if (ferry.getCarQueue().contains(this)) {
                return;
            }
            ferry.getCarQueue().offerLast(this);
            this.changeState();
        } else {
            ferry.getCarOnFerry().offerLast(this);
            ferry.getCarQueue().remove(this);
            ferry.setWeightLoaded(ferry.getWeightLoaded() + this.getWeight());
            ferry.setSpaceOnFerry(ferry.getSpaceOnFerry() + this.getArea());
            ferry.setCountCarsOnFerry(ferry.getCountCarsOnFerry() + 1);
            this.changeState();
            logger.info(this);
        }
    }

    public int getId() {
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

    public CarState getCarState() {
        return carState;
    }

    public void setCarState(CarState carState) {
        this.carState = carState;
    }

    public static int getCounter() {
        return counter;
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