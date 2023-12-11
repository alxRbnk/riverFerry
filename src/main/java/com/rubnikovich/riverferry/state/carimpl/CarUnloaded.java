package com.rubnikovich.riverferry.state.carimpl;

import com.rubnikovich.riverferry.state.StateCar;

public class CarUnloaded implements StateCar {

    public void doAction() {
        System.out.println("Car is unloaded");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("unloaded");
        return sb.toString();
    }
}
