package com.rubnikovich.riverferry.state.carimpl;

import com.rubnikovich.riverferry.state.StateCar;

public class CarOnFerry implements StateCar {

    public void doAction() {
        System.out.println("Car on ferry");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("onFerry");
        return sb.toString();
    }
}

