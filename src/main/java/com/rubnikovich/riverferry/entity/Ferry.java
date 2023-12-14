package com.rubnikovich.riverferry.entity;

import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.util.CustomLock;
import com.rubnikovich.riverferry.util.InfoFerry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Ferry implements Callable<InfoFerry> {
    public static final Logger logger = LogManager.getLogger();
    public static final int LIMIT_COUNT = 5;
    public static final int LIMIT_AREA = 50;
    public static final int LIMIT_WEIGHT = 12_000;
    private static final int TOTAL_CARS = Car.getCarsQueue().size();
    private static final String LOCK = "Ferry lock";
    private static final String UNLOCK = "Ferry unlock";
    private Stack<Car> carsOnFerry = new Stack<>();
    private Stack<Car> carsUnloaded = new Stack<>();
    private int spaceOnFerry;
    private int countCarsOnFerry;
    private int weightLoaded;
    private int loadedCarsOnFerry;
    private int countTrips;

    private static class InstanceFerry {
        private static final Ferry instance = new Ferry();
    }

    private Ferry() {
    }

    public static Ferry getInstance() {
        return InstanceFerry.instance;
    }

    public Stack<Car> getCarsOnFerry() {
        return carsOnFerry;
    }

    public int getCountCarsOnFerry() {
        return countCarsOnFerry;
    }

    public void setCountCarsOnFerry(int countCarsOnFerry) {
        this.countCarsOnFerry = countCarsOnFerry;
    }

    public int getSpaceOnFerry() {
        return spaceOnFerry;
    }

    public void setSpaceOnFerry(int spaceOnFerry) {
        this.spaceOnFerry = spaceOnFerry;
    }

    public int getWeightLoaded() {
        return weightLoaded;
    }

    public void setWeightLoaded(int weightLoaded) {
        this.weightLoaded = weightLoaded;
    }

    public Stack<Car> getCarsUnloaded() {
        return carsUnloaded;
    }

    @Override
    public InfoFerry call() throws CustomException {
        try {
            TimeUnit.MILLISECONDS.sleep(50);
            while (carsUnloaded.size() != TOTAL_CARS) {
                try {
                    CustomLock.lock.lock();
                    logger.info(LOCK);
                    logger.info(this);
                    unloadFerry();
                } finally {
                    CustomLock.lock.unlock();
                    logger.info(UNLOCK);
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            throw new CustomException(e);
        }
        return new InfoFerry(loadedCarsOnFerry, countTrips);
    }

    private void unloadFerry() {
        for (int i = 0; i < LIMIT_COUNT; i++) {
            if (carsOnFerry.isEmpty()) {
                continue;
            }
            carsUnloaded.push(carsOnFerry.pop());
            carsUnloaded.peek().changeState();
            logger.info(carsUnloaded.peek());
            countCarsOnFerry--;
            loadedCarsOnFerry++;
        }
        spaceOnFerry = 0;
        weightLoaded = 0;
        countTrips++;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Ferry{");
        sb.append("spaceOnFerry=").append(spaceOnFerry);
        sb.append(", countCarsOnFerry=").append(countCarsOnFerry);
        sb.append(", weightLoaded=").append(weightLoaded);
        sb.append('}');
        return sb.toString();
    }
}
