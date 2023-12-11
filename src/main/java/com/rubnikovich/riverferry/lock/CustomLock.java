package com.rubnikovich.riverferry.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomLock {
    public static Lock lock = new ReentrantLock();

    private CustomLock(){
    }

}
