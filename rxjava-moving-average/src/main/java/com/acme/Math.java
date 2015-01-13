package com.acme;

import java.util.List;

/**
 * Created by mpollack on 12/19/14.
 */
public class Math {

    public static Double avg(List<Double> data) {
        double sum = 0;
        double count = 0;
        for(Double d : data) {
            count++;
            sum += d;
        }
        return sum/count;
    }
}
