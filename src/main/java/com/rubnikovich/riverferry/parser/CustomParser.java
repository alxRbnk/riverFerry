package com.rubnikovich.riverferry.parser;

import com.rubnikovich.riverferry.entity.Car;
import com.rubnikovich.riverferry.exception.CustomException;

import java.util.Deque;

public interface CustomParser {
    Deque<Car> parseFile(String path) throws CustomException;
}
