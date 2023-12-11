package com.rubnikovich.riverferry.parser.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.rubnikovich.riverferry.exception.CustomException;
import com.rubnikovich.riverferry.entity.Car;
import com.rubnikovich.riverferry.parser.CustomParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CustomParserImpl implements CustomParser {

    public Queue<Car> parseFile(String path) throws CustomException {
        Queue<Car> listCars = new LinkedList<>();
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
