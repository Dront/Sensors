package com.dront.sensors;

public class MovingFilter {
    //do not ask me why
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

    public AccRecord filter(AccRecord cur, AccRecord next){
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

    private AccRecord lowPass(AccRecord cur, AccRecord next){
        AccRecord res = new AccRecord();
        for (int i = 0; i < 3; i++){
            res.values[i] = FILTER_ALPHA*cur.values[i] + (1 - FILTER_ALPHA)*next.values[i];
        }
        res.countAbs();
        return res;
    }

    private AccRecord filterLowPassMod1(AccRecord cur, AccRecord next){
        AccRecord res = new AccRecord();
        for (int i = 0; i < 3; i++){
            float delta = Math.abs(cur.values[i] - next.values[i]);
            if (delta < FILTER_DELTA_HIGH){
                res.values[i] = FILTER_ALPHA*cur.values[i] + (1 - FILTER_ALPHA)*next.values[i];
            } else {
                res.values[i] = next.values[i];
            }
        }
        res.countAbs();
        return res;
    }

    private AccRecord filterLowPassMod2(AccRecord cur, AccRecord next){
        AccRecord res = new AccRecord();
        for (int i = 0; i < 3; i++){
            float delta = Math.abs(cur.values[i] - next.values[i]);
            if ( delta > FILTER_DELTA_LOW && delta < FILTER_DELTA_HIGH){
                res.values[i] = FILTER_ALPHA*cur.values[i] + (1 - FILTER_ALPHA)*next.values[i];
            } else if (delta < FILTER_DELTA_LOW){
                res.values[i] = cur.values[i];
            } else if (delta > FILTER_DELTA_HIGH){
                res.values[i] = next.values[i];
            }
        }
        res.countAbs();
        return res;
    }

    public FilterType getType(){
        return type;
    }
}
