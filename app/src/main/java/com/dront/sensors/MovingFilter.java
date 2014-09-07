package com.dront.sensors;

public class MovingFilter {
    private static final float FILTER_ALPHA = 0.90f;
    private static final float FILTER_DELTA_HIGH = 4.0f;
    private static final float FILTER_DELTA_LOW = 1.0f;

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

    public float[] filter(float[] cur, float[] next){
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

    private float[] lowPass(float[] cur, float[] next){
        float[] result = new float[3];
        for (int i = 0; i < 3; i++){
            result[i] = FILTER_ALPHA * cur[i] + (1 - FILTER_ALPHA) * next[i];
        }
        return result;
    }

    private float[] filterLowPassMod1(float[] cur, float[] next){
        float[] result = new float[3];
        for (int i = 0; i < 3; i++){
            float delta = Math.abs(cur[i] - next[i]);
            if (delta < FILTER_DELTA_HIGH){
                result[i] = FILTER_ALPHA * cur[i] + (1 - FILTER_ALPHA) * next[i];
            } else {
                result[i] = next[i];
            }
        }
        return result;
    }

    private float[] filterLowPassMod2(float[] cur, float[] next){
        float[] result = new float[3];
        for (int i = 0; i < 3; i++){
            float delta = Math.abs(cur[i] - next[i]);
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
