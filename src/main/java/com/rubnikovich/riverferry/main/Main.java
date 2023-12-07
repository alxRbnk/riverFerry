package com.rubnikovich.riverferry.main;

import com.rubnikovich.riverferry.entity.Car;
import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.parser.CustomParser;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main {
    static CustomParser customReader = new CustomParser();
    static List<Car> cars = customReader.getCars();
    static Queue<Car> queue = new LinkedList<>();
    static Queue<Car> queueCars;
    static int limit = 10;
    
    public static void main(String[] args) throws InterruptedException, CustomException {
        customReader.parseFile("files/file.csv");
        Main object = new Main();
        System.out.println(cars);
        queueCars = new LinkedList<>(cars);

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    object.ferryLoaded();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    object.ferryLeaving();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    public void ferryLoaded() throws InterruptedException {
        while (queueCars.size() != 0) {
            synchronized (this) {
                for (int i = 0; i < limit; i++) {
                    Thread.sleep(200);
                    queue.offer(queueCars.poll());
                    System.out.println(i + " load");
                    if (queue.size() == limit || queueCars.isEmpty()) {
                        System.out.println("ferry is leaving");
                        wait();
                        if (queueCars.size() == 0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public void ferryLeaving() throws InterruptedException {
        while (true) {
            synchronized (this) {
                notify();
                if (queue.size() == limit || queueCars.size() == 0) {
                    Thread.sleep(200);
                    while (!queue.isEmpty()) {
                        System.out.println(queue.poll() + " unloaded");
                    }
                    System.out.println("the ferry is empty");
                    if (queueCars.size() == 0) {
                        break;
                    }
                }
            }
        }
    }
}
