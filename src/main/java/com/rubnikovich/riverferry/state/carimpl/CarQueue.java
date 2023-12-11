package com.rubnikovich.riverferry.state.carimpl;

import com.rubnikovich.riverferry.state.StateCar;

public class CarQueue implements StateCar {

    public void doAction() {
        System.out.println("Car in queue");
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("queue");
        return sb.toString();
    }
}
