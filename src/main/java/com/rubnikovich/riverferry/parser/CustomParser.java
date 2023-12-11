package com.rubnikovich.riverferry.parser;

import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.entity.Car;

import java.util.Queue;

public interface CustomParser {
    Queue<Car> parseFile(String path) throws CustomException;
}
