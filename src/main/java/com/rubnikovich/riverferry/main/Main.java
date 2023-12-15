package com.rubnikovich.riverferry.main;

import com.rubnikovich.riverferry.entity.Car;
import com.rubnikovich.riverferry.entity.Ferry;
import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.parser.CustomParser;
import com.rubnikovich.riverferry.parser.impl.CustomParserImpl;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws CustomException, InterruptedException {
        CustomParser customParser = new CustomParserImpl();
        Car car = new Car(customParser.parseFile("files/file.csv"));
        Ferry.logger.info(Car.getCarsQueue());

        ExecutorService executorServiceFerry = Executors.newSingleThreadExecutor();
        ExecutorService executorServiceCars = Executors.newSingleThreadExecutor();
        executorServiceFerry.execute(Ferry.getInstance());
        executorServiceCars.execute(car);
        executorServiceFerry.shutdown();
        executorServiceCars.shutdown();

        executorServiceFerry.awaitTermination(50, TimeUnit.SECONDS);
        Ferry ferry = Ferry.getInstance();
        System.out.println(ferry.getCarsOnFerry().size());
        System.out.println(ferry.getCarsUnloaded().size());

    }
}
