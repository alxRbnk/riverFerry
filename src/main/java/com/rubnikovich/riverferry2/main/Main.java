package com.rubnikovich.riverferry2.main;

import com.rubnikovich.riverferry2.entity.Car;
import com.rubnikovich.riverferry2.entity.Ferry;
import com.rubnikovich.riverferry2.exception.CustomException;
import com.rubnikovich.riverferry2.parser.CustomParser;
import com.rubnikovich.riverferry2.parser.impl.CustomParserImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) throws CustomException {
        CustomParser customParser = new CustomParserImpl();
        Ferry ferry = Ferry.getInstance();
        Deque<Car> deque = customParser.parseFile("files/file.csv");

        ExecutorService executorServiceFerry = Executors.newSingleThreadExecutor();
        ExecutorService executorServiceCars = Executors.newFixedThreadPool(deque.size());

        for (Car car : deque) {
            executorServiceCars.execute(car);
        }
        executorServiceFerry.execute(ferry);
        executorServiceFerry.shutdown();
        executorServiceCars.shutdown();

        try {
            executorServiceFerry.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            logger.error(e);
            Thread.currentThread().interrupt();
        }
        logger.info("onFerry=" + ferry.getCarOnFerry().size() + " \nloaded=" + ferry.getCarLoaded());
    }
}
