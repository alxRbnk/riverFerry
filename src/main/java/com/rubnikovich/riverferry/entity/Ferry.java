package com.rubnikovich.riverferry.entity;

import java.util.ArrayDeque;
import java.util.Queue;

public class Ferry{
    private static final int LIMIT = 10;
    private static final int WEIGHT_LIMIT = 25000;
    private static final int AREA_LIMIT = 200;
    private static Ferry ferry;
    private Queue<Car> cars;

    public Queue<Car> getCars() {
        return cars;
    }

}
