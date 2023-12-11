package com.rubnikovich.riverferry.main;

import com.rubnikovich.riverferry.entity.Car;
import com.rubnikovich.riverferry.entity.Cars;
import com.rubnikovich.riverferry.entity.Ferry;
import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.parser.CustomParser;
import com.rubnikovich.riverferry.parser.impl.CustomParserImpl;

public class Main {
    public static void main(String[] args) throws InterruptedException, CustomException {
        CustomParser customParser = new CustomParserImpl();
        Cars.carsQueue = customParser.parseFile("files/file.csv");
        System.out.println(Cars.carsQueue);

        Ferry ferry = new Ferry();
        Car car = new Car();
        ferry.start();
        car.start();


        ferry.join();
        car.join();

        System.out.println("----" + Cars.carsQueue);
        System.out.println("----" + Ferry.carsOnFerry);
        System.out.println("----" + Cars.carsUnloaded);








    }
}
