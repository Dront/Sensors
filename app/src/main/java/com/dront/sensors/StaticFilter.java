package com.dront.sensors;

import java.util.ArrayList;
import java.util.Arrays;

public class StaticFilter {

    //must be odd number
    public static final int MEDIAN_WINDOW = 3;

    public enum FilterType{
        MEDIAN
    }

    private FilterType type;

    //constructor
    public StaticFilter(FilterType t){
        type = t;
    }

    public ArrayList<AccRecord> filter(ArrayList<AccRecord> data){
        switch (type){
            case MEDIAN:
                return median(data);
            default:
                return null;
        }
    }

    //ass
    private ArrayList<AccRecord> median(ArrayList<AccRecord> data){
        if (data.size() < MEDIAN_WINDOW){
            return null;
        }

        int shift = MEDIAN_WINDOW / 2;
        ArrayList<AccRecord> res = new ArrayList<AccRecord>(data.size() - shift * 2);

        for (int i = shift; i < data.size() - shift; i++){
            AccRecord[] tmpList = new AccRecord[MEDIAN_WINDOW];
            for (int j = 0; j < MEDIAN_WINDOW; j++){
                tmpList[j] = data.get(i + j - shift);
            }
            Arrays.sort(tmpList, new AccRecordMeanComparator());

            res.add(tmpList[shift]);
        }

        return res;
    }

    //getters
    public FilterType getType() {
        return type;
    }

    //setters
    public void setType(FilterType type) {
        this.type = type;
    }
}
