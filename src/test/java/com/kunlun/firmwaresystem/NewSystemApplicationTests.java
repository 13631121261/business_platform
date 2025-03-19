package com.kunlun.firmwaresystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class NewSystemApplicationTests {

    @Test
    void contextLoads() {

    }
    public double calculateVariance(double[] data) {
        double mean = Arrays.stream(data).average().orElse(0);
        double sumOfSquares = Arrays.stream(data).map(x -> (x - mean) * (x - mean)).sum();
        return sumOfSquares / data.length;
    }
    public double calculateMean(double[] data) {
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        return sum / data.length;
    }

    public double calculateVariance1(double[] data) {
        double mean = calculateMean(data);
        double sumOfSquares = 0;
        for (int i = 0; i < data.length; i++) {
            double diff = data[i] - mean;
            sumOfSquares += diff * diff;
        }
        return sumOfSquares / data.length;
    }

    public static void test( List<Double> numbers) {








    }
}
