package com.rubnikovich.riverferry2.parser;

import com.rubnikovich.riverferry2.entity.Car;
import com.rubnikovich.riverferry2.exception.CustomException;

import java.util.Deque;

public interface CustomParser {
    Deque<Car> parseFile(String path) throws CustomException;
}
