package com.rubnikovich.riverferry.main;

import com.rubnikovich.riverferry.entity.Car;
import com.rubnikovich.riverferry.entity.Ferry;
import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.parser.CustomParser;
import com.rubnikovich.riverferry.parser.impl.CustomParserImpl;
import com.rubnikovich.riverferry.util.InfoFerry;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws CustomException {
        CustomParser customParser = new CustomParserImpl();
        Car car = new Car(customParser.parseFile("files/file.csv"));
        Ferry.logger.info(Car.getCarsQueue());

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<InfoFerry> future = executorService.submit(Ferry.getInstance());
        executorService.submit(car);
        executorService.shutdown();
        InfoFerry info = null;
        try {
            info = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new CustomException(e);
        }
        Ferry.logger.info(info);
    }
}
