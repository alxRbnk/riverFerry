package com.rubnikovich.riverferry.main;

import com.rubnikovich.riverferry.entity.Car;
import com.rubnikovich.riverferry.entity.Cars;
import com.rubnikovich.riverferry.entity.Ferry;
import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.parser.CustomParser;
import com.rubnikovich.riverferry.parser.impl.CustomParserImpl;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, CustomException, ExecutionException {
        CustomParser customParser = new CustomParserImpl();
        Cars.carsQueue = customParser.parseFile("files/file.csv");
        Ferry.logger.info(Cars.carsQueue);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> future = executorService.submit(Ferry.getInstance());
        executorService.submit(new Car());
        executorService.shutdown();
        Integer result = future.get();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Ferry.logger.info("Loaded cars - " + result);

    }
}
