package com.dront.sensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

public class AccInfo {
    //do not ask me why
    private static final int DEFAULT_SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;
    private static final int DEFAULT_ARR_SIZE = 500;
    private static final double DEFAULT_FLIGHT_GRAVITY = 5.0;
    private static final double MIN_FLIGHT_TIME = 0.2;

    private  static volatile AccInfo instance;

    private Float curX, curY, curZ, curAbs;
    private ArrayList<AccRecord> data;
    private Integer delay;
    private boolean enabled;
    private String info;
    private Double resolution;
    private Double flightGravityMax;

    //private because it's a singleton
    private AccInfo(Sensor s){
        delay = DEFAULT_SENSOR_DELAY;
        info = s.toString();
        resolution = (double)s.getResolution();
        flightGravityMax = DEFAULT_FLIGHT_GRAVITY;
        enabled = false;
        zeroValues();
        data = new ArrayList<AccRecord>(DEFAULT_ARR_SIZE);
    }

    //private methods
    private void zeroValues(){
        curX = curY = curZ = curAbs = 0.0f;
    }

    private ArrayList<Double> findFlightIntervals(){
        ArrayList<Double> res = new ArrayList<Double>();

        FlightInterval tmpInterval = new FlightInterval();
        for(AccRecord cur: data){
            if (cur.getAbs() > flightGravityMax){
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

    private double[] countHeights(ArrayList<Double> times){
        double[] res = new double[times.size()];

        for (int i = 0; i < times.size(); i++){
            Double tmp = times.get(i) / 2;
            res[i] = SensorManager.GRAVITY_EARTH * tmp * tmp / 2;
        }

        return res;
    }

    public double[] getFlights(){
        ArrayList<Double> intervals = findFlightIntervals();

        if (intervals.isEmpty()){
            return null;
        }
        return countHeights(intervals);
    }

    //public methods
    public static AccInfo getInstance(Sensor s){
        if (instance == null ){
            synchronized (AccInfo.class){
                if (instance == null){
                    instance = new AccInfo(s);
                    Log.d(Constants.LOG_SINGLETON, "create singleton");
                }
            }
        }
        return instance;
    }

    public static AccInfo getInstance(){
        Log.d(Constants.LOG_SINGLETON, "return singleton");
        return instance;
    }

    public void smallTick(float[] newVal){
        if (data.size() != 0){
            float[] curVal = {curX, curY, curZ};
            MovingFilter accFilter = new MovingFilter(MovingFilter.FilterType.LOW_PASS_MOD_1);
            curVal = accFilter.filter(curVal, newVal);
            curX = curVal[0];
            curY = curVal[1];
            curZ = curVal[2];
        } else {
            curX = newVal[0];
            curY = newVal[1];
            curZ = newVal[2];
        }

        float mean = (float) Math.sqrt(curX*curX + curY*curY + curZ*curZ);
        curAbs = mean;
        data.add(new AccRecord(curX, curY, curZ, mean));
    }

    public void enable() {
        zeroValues();
        data.clear();
        this.enabled = true;
    }

    public void disable(){
        data.trimToSize();
        this.enabled = false;
    }

    public boolean isDataEmpty(){
        return data.isEmpty();
    }

    //getters
    public Float getLastAbs() {
        return curAbs;
    }

    public Float getLastX() {
        return curX;
    }

    public Float getLastY() {
        return curY;
    }

    public Float getLastZ() {
        return curZ;
    }

    public Integer getDelay() {
        return delay;
    }

    public String getInfo() {
        return info;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public ArrayList<AccRecord> getData(){
        return data;
    }

    public Double getResolution(){
        return resolution;
    }

    //setters
    public void setDelay(Integer delay) {
        this.delay = delay;
    }

}
