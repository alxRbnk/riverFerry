package com.rubnikovich.riverferry.entity;

import com.rubnikovich.riverferry.lock.CustomLock;
import com.rubnikovich.riverferry.state.StateCar;
import com.rubnikovich.riverferry.state.carimpl.CarQueue;
import com.rubnikovich.riverferry.state.carimpl.CarOnFerry;
import com.rubnikovich.riverferry.state.carimpl.CarUnloaded;

import java.util.Objects;

public class Car extends Thread {
    private int id;
    private int weight;
    private int area;
    private StateCar stateCar;

    public Car() {
    }

    public Car(int weight, int area, int id) {
        this.id = id;
        this.weight = weight;
        this.area = area;
        this.stateCar = new CarQueue();
    }

    public void load() throws InterruptedException {
        while (Cars.carsQueue.size() != 0) {
            CustomLock.lock.lock();
            System.out.println("Car lock");
            try {
                loadCar();
            } finally {
                System.out.println("countCarFerry " + Ferry.carsOnFerry.size() );
                System.out.println("Car unlock");
                CustomLock.lock.unlock();
                Thread.sleep(500);
            }
        }
    }

    public void loadCar() throws InterruptedException {
        for (int i = 0; i < Ferry.LIMIT; i++) {
            if (Cars.carsQueue.size() == 0) {
                continue;
            }
            Ferry.carsOnFerry.push(Cars.carsQueue.poll());
            Ferry.carsOnFerry.peek().changeState();
            System.out.println(Ferry.carsOnFerry.peek());
            Ferry.countCars++;
            Thread.sleep(1000);
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
            stateCar = new CarOnFerry();
        } else if (Cars.carsUnloaded.contains(this)) {
            stateCar = new CarUnloaded();
        } else if (Cars.carsQueue.contains(this)) {
            stateCar = new CarQueue();
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

    public StateCar getStateCar() {
        return stateCar;
    }

    public void setStateCar(StateCar stateCar) {
        this.stateCar = stateCar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (id != car.id) return false;
        if (weight != car.weight) return false;
        if (area != car.area) return false;
        return Objects.equals(stateCar, car.stateCar);
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
        sb.append(",").append(stateCar);
        sb.append('}');
        return sb.toString();
    }
}
