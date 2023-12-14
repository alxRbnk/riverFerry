package com.rubnikovich.riverferry.main;

import com.rubnikovich.riverferry.entity.Car;
import com.rubnikovich.riverferry.entity.Ferry;
import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.parser.CustomParser;
import com.rubnikovich.riverferry.parser.impl.CustomParserImpl;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws CustomException {
        CustomParser customParser = new CustomParserImpl();
        Car car = new Car(customParser.parseFile("files/file.csv"));
        Ferry.logger.info(Car.getCarsQueue());

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Integer> future = executorService.submit(Ferry.getInstance());
        executorService.submit(car);
        executorService.shutdown();
        Integer result = null;
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new CustomException(e);
        }

        Ferry.logger.info(result);
    }
}
