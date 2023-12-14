package com.rubnikovich.riverferry.util;

public class InfoFerry {
    private int countCar;
    private int countTrips;

    public InfoFerry(int countCar, int countTrips) {
        this.countCar = countCar;
        this.countTrips = countTrips;
    }

    public int getCountCar() {
        return countCar;
    }

    public void setCountCar(int countCar) {
        this.countCar = countCar;
    }

    public int getCountTrips() {
        return countTrips;
    }

    public void setCountTrips(int countTrips) {
        this.countTrips = countTrips;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Info{");
        sb.append("countCar=").append(countCar);
        sb.append(", countTrips=").append(countTrips);
        sb.append('}');
        return sb.toString();
    }
}
