package com.rubnikovich.riverferry2.parser.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.rubnikovich.riverferry2.entity.Car;
import com.rubnikovich.riverferry2.exception.CustomException;
import com.rubnikovich.riverferry2.parser.CustomParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class CustomParserImpl implements CustomParser {

    public Deque<Car> parseFile(String path) throws CustomException {
        Deque<Car> listCars = new ArrayDeque<>();
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                for (String line : nextLine) {
                    int[] arrayInt = Arrays.stream(line.split(";"))
                            .mapToInt(Integer::parseInt).toArray();
                    listCars.add(new Car(arrayInt[0], arrayInt[1], arrayInt[2]));
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new CustomException(e);
        }
        return listCars;
    }

}
