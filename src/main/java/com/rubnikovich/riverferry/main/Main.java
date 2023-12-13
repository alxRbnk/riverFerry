package com.rubnikovich.riverferry.main;

import com.rubnikovich.riverferry.entity.Car;
import com.rubnikovich.riverferry.entity.Cars;
import com.rubnikovich.riverferry.entity.Ferry;
import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.parser.CustomParser;
import com.rubnikovich.riverferry.parser.impl.CustomParserImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException, CustomException {
        CustomParser customParser = new CustomParserImpl();
        Cars.carsQueue = customParser.parseFile("files/file.csv");
        System.out.println(Cars.carsQueue);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(Ferry.getInstance());
        executorService.submit(new Car());

        executorService.shutdown();

        executorService.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("----" + Cars.carsQueue);
        System.out.println("----" + Ferry.carsOnFerry);
        System.out.println("----" + Cars.carsUnloaded);

    }
}
