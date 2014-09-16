package com.dront.sensors;

import android.hardware.SensorManager;
import android.os.AsyncTask;

import java.util.ArrayList;

public class FlightAnalyzer extends AsyncTask<Void, Void, Void> {

    private static final double DEFAULT_FLIGHT_GRAVITY = 5.0;
    private static final double MIN_FLIGHT_TIME = 0.2;

    private ArrayList<AccRecord> data;
    private static ArrayList<Double> flightHeights;

    static {
        flightHeights = new ArrayList<Double>();
    }

    public FlightAnalyzer(ArrayList<AccRecord> records){
        data = new ArrayList<AccRecord>(records.size());
        data.addAll(records);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ArrayList<Double> intervals = findFlightIntervals();

        if (intervals.isEmpty()){
            return null;
        }
        flightHeights.addAll(countHeights(intervals));

        return null;
    }

    private ArrayList<Double> countHeights(ArrayList<Double> times){
        ArrayList<Double> res = new ArrayList<Double>(times.size());

        for (int i = 0; i < times.size(); i++){
            Double tmp = times.get(i) / 2;
            res.add(SensorManager.GRAVITY_EARTH * tmp * tmp / 2);
        }

        return res;
    }

    private ArrayList<Double> findFlightIntervals(){
        ArrayList<Double> res = new ArrayList<Double>();

        FlightInterval tmpInterval = new FlightInterval();
        for(AccRecord cur: data){
            if (cur.getAbs() > DEFAULT_FLIGHT_GRAVITY){
                if (tmpInterval.start != 0){
                    double time = ((double)tmpInterval.end - tmpInterval.start) / 1000;
                    if (time > MIN_FLIGHT_TIME){
                        res.add(time);
                    }
                    tmpInterval = new FlightInterval();
                }
                continue;
            }

            if (tmpInterval.start == 0){
                tmpInterval.start = cur.getTime();
                tmpInterval.end = cur.getTime();
            } else {
                tmpInterval.end = cur.getTime();
            }
        }

        return res;
    }

    public static int getFlightCount(){
        return flightHeights.size();
    }

    public static double getLastHeight(){
        if (flightHeights.size() == 0){
            return 0;
        }
        return flightHeights.get(flightHeights.size() - 1);
    }
}
