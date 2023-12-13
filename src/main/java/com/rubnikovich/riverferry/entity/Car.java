package com.rubnikovich.riverferry.entity;

import com.rubnikovich.riverferry.util.CustomLock;
import com.rubnikovich.riverferry.state.CarState;
import com.rubnikovich.riverferry.state.impl.CarQueue;
import com.rubnikovich.riverferry.state.impl.CarOnFerry;
import com.rubnikovich.riverferry.state.impl.CarUnloaded;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Car implements Runnable {
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

    private void load() throws InterruptedException {
        while (Cars.carsQueue.size() != 0) {
            CustomLock.lock.lock();
            System.out.println("Car lock");
            try {
                loadCar();
            } finally {
                System.out.println("Car unlock");
                CustomLock.lock.unlock();
                TimeUnit.MILLISECONDS.sleep(200);
            }
        }
    }

    private void loadCar() throws InterruptedException {
        for (int i = 0; i < Ferry.LIMIT; i++) {
            if (Cars.carsQueue.size() == 0) {
                continue;
            }
            Ferry.carsOnFerry.push(Cars.carsQueue.poll());
            Ferry.carsOnFerry.peek().changeState();
            System.out.println(Ferry.carsOnFerry.peek());
            Ferry.countCars++;
        }
    }

    @Override
    public void run() {
        try {
            load();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeState() {
        if (Ferry.carsOnFerry.contains(this)) {
            carState = new CarOnFerry();
        } else if (Cars.carsUnloaded.contains(this)) {
            carState = new CarUnloaded();
        } else if (Cars.carsQueue.contains(this)) {
            carState = new CarQueue();
        }
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


    public void setCarState(CarState carState) {
        this.carState = carState;
    }

    public String getCarState() {
        return carState.getState();
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
