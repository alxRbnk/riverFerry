package com.rubnikovich.riverferry.entity;

import com.rubnikovich.riverferry.util.CustomLock;
import com.rubnikovich.riverferry.state.CarState;
import com.rubnikovich.riverferry.state.impl.CarQueue;
import com.rubnikovich.riverferry.state.impl.CarOnFerry;
import com.rubnikovich.riverferry.state.impl.CarUnloaded;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Car implements Runnable {
    private static final String LOCK = "Car lock";
    private static final String UNLOCK = "Car unlock";
    private int id;
    private int weight;
    private int area;
    private CarState carState;

    public Car() {
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

    public void changeState() {
        if (Ferry.getInstance().getCarsOnFerry().contains(this)) {
            carState = new CarOnFerry();
        } else if (Cars.carsUnloaded.contains(this)) {
            carState = new CarUnloaded();
        } else if (Cars.carsQueue.contains(this)) {
            carState = new CarQueue();
        }
    }

    @Override
    public void run() {
        try {
            while (Cars.carsQueue.size() != 0) {
                CustomLock.lock.lock();
                Ferry.logger.info(LOCK);
                try {
                    loadCar();
                } finally {
                    Ferry.logger.info(UNLOCK);
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
            if(isInvalid()){
                continue;
            }
            ferry.getCarsOnFerry().push(Cars.carsQueue.poll());
            ferry.getCarsOnFerry().peek().changeState();
            Ferry.logger.info(ferry.getCarsOnFerry().peek());
            int countCarOnFerry = ferry.getCountCarsOnFerry();
            ferry.setCountCarsOnFerry(++countCarOnFerry);
            ferry.setSpaceOnFerry(ferry.getSpaceOnFerry() + ferry.getCarsOnFerry().peek().getArea());
            ferry.setWeightLoaded(ferry.getWeightLoaded() + ferry.getCarsOnFerry().peek().getWeight());
        }
    }

    private boolean isInvalid(){
        Ferry ferry = Ferry.getInstance();
        return Cars.carsQueue.isEmpty() ||
                (ferry.getSpaceOnFerry() + Cars.carsQueue.peek().getArea()) > Ferry.LIMIT_AREA ||
                (ferry.getWeightLoaded() + Cars.carsQueue.peek().getWeight()) > Ferry.LIMIT_WEIGHT;
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
