package com.rubnikovich.riverferry.main;

import com.rubnikovich.riverferry.entity.Cars;
import com.rubnikovich.riverferry.entity.Ferry;
import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.parser.CustomParser;
import com.rubnikovich.riverferry.parser.impl.CustomParserImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) throws CustomException {
        CustomParser customParser = new CustomParserImpl();
        Cars cars = Cars.getInstance();
        try {
            cars.setCarsQueue(customParser.parseFile("files/file.csv"));
        } catch (CustomException e) {
            throw new CustomException(e);
        }
        Ferry ferry = Ferry.getInstance();

        logger.info(cars.getCarsQueue());

        ExecutorService executorServiceFerry = Executors.newSingleThreadExecutor();
        ExecutorService executorServiceCars = Executors.newSingleThreadExecutor();
        executorServiceCars.execute(cars);
        executorServiceFerry.execute(ferry);
        executorServiceFerry.shutdown();
        executorServiceCars.shutdown();

        try {
            executorServiceFerry.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            logger.error(e);
            Thread.currentThread().interrupt();
        }
        logger.info("onFerry=" + ferry.getCarsOnFerry().size() + " \nloaded=" + cars.getCarsLoaded().size());
    }
}
