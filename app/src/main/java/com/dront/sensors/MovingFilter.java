package com.dront.sensors;

import java.util.ArrayList;

public class MovingFilter {
    private static final double FILTER_ALPHA = 0.90;
    private static final double FILTER_DELTA_HIGH = 4.0;
    private static final double FILTER_DELTA_LOW = 1.0;

    private FilterType type;

    //filter types
    public enum FilterType {
        LOW_PASS,
        LOW_PASS_MOD_1,
        LOW_PASS_MOD_2
    }

    //constructor
    public MovingFilter(FilterType t){
        type = t;
    }

    public double[] filter(double[] cur, double[] next){
        switch (type){
            case LOW_PASS:
                return lowPass(cur, next);
            case LOW_PASS_MOD_1:
                return filterLowPassMod1(cur, next);
            case LOW_PASS_MOD_2:
                return filterLowPassMod2(cur, next);
            default:
                return null;
        }
    }

    private double[] lowPass(double[] cur, double[] next){
        double[] result = new double[3];
        for (int i = 0; i < 3; i++){
            result[i] = FILTER_ALPHA * cur[i] + (1 - FILTER_ALPHA) * next[i];
        }
        return result;
    }

    private double[] filterLowPassMod1(double[] cur, double[] next){
        double[] result = new double[3];
        for (int i = 0; i < 3; i++){
            double delta = Math.abs(cur[i] - next[i]);
            if (delta < FILTER_DELTA_HIGH){
                result[i] = FILTER_ALPHA * cur[i] + (1 - FILTER_ALPHA) * next[i];
            } else {
                result[i] = next[i];
            }
        }
        return result;
    }

    private double[] filterLowPassMod2(double[] cur, double[] next){
        double[] result = new double[3];
        for (int i = 0; i < 3; i++){
            double delta = Math.abs(cur[i] - next[i]);
            if ( delta > FILTER_DELTA_LOW && delta < FILTER_DELTA_HIGH){
                result[i] = FILTER_ALPHA * cur[i] + (1 - FILTER_ALPHA) * next[i];
            } else if (delta < FILTER_DELTA_LOW){
                result[i] = cur[i];
            } else if (delta > FILTER_DELTA_HIGH){
                result[i] = next[i];
            }
        }
        return result;
    }

    public FilterType getType(){
        return type;
    }
}
