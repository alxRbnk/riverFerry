package com.rubnikovich.riverferry.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Car implements Comparable<Car>{
    public static final Logger logger = LogManager.getLogger();
    private int weight;
    private int area;
    private int id;

    public Car() {
    }

    public Car(int weight, int area, int id) {
        setWeight(weight);
        setArea(area);
        setId(id);
    }


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight > 0) {
            this.weight = weight;
        } else {
            logger.error("invalid weight");
        }
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        if (area > 0) {
            this.area = area;
        } else {
            logger.error("invalid area");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id > 0) {
            this.id = id;
        } else {
            logger.error("invalid id");
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (weight != car.weight) return false;
        if (area != car.area) return false;
        return id == car.id;
    }

    @Override
    public int hashCode() {
        int result = weight;
        result = 31 * result + area;
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Car[");
        sb.append(id);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int compareTo(Car car) {
        return Integer.compare(this.id, car.id);
    }
}
