package com.rubnikovich.riverferry.parser;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.rubnikovich.riverferry.entity.Car;
import com.rubnikovich.riverferry.exception.CustomException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomParser {
    private List<Car> cars = new ArrayList<>();

    public void parseFile(String path) throws CustomException {
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                for (String line : nextLine) {
                    int[] arrayInt = Arrays.stream(line.split(";"))
                            .mapToInt(Integer::parseInt).toArray();
                    cars.add(new Car(arrayInt[0],arrayInt[1],arrayInt[2]));
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new CustomException(e);
        }
    }

    public List<Car> getCars(){
        return Collections.unmodifiableList(cars);
    }
}
