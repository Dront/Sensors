package com.dront.sensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

public class AccInfo {
    //dem fields
    //do not ask me why

    public static final int DEFAULT_BIG_TIC_DELAY = 200;
    private static final int DEFAULT_SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;
    private static final int DEFAULT_ARR_SIZE = 500;
    private static final double DEFAULT_FLIGHT_GRAVITY = 5.0;
    private static final double MIN_FLIGHT_TIME = 0.2;

    private  static volatile AccInfo instance;

    private Double curX, curY, curZ;
    private Double sumX, sumY, sumZ;
    private Double meanX, meanY, meanZ, meanAbs;
    private ArrayList<AccRecord> data;
    private Integer counter;
    private Integer delay;
    private boolean enabled;
    private String info;
    private Double resolution;
    private Double flightGravityMax;
    private Integer bigTickDelay;

    //private because it's a singleton
    private AccInfo(Sensor s){
        delay = DEFAULT_SENSOR_DELAY;
        bigTickDelay = DEFAULT_BIG_TIC_DELAY;
        info = s.toString();
        resolution = (double)s.getResolution();
        flightGravityMax = DEFAULT_FLIGHT_GRAVITY;
        enabled = false;
        zeroValues();
        meanX = meanY = meanZ = meanAbs = 0.0;
        data = new ArrayList<AccRecord>(DEFAULT_ARR_SIZE);
    }

    //private methods
    private void zeroValues(){
        sumX = sumY = sumZ = 0.0;
        curX = curY = curZ = 0.0;
        counter = 0;
    }

    private ArrayList<Double> findFlightIntervals(){
        ArrayList<Double> res = new ArrayList<Double>();

        FlightInterval tmpInterval = new FlightInterval();
        for(AccRecord cur: data){
            if (cur.meanVal > flightGravityMax){
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
                tmpInterval.start = cur.time;
                tmpInterval.end = cur.time;
            } else {
                tmpInterval.end = cur.time;
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

    public void smallTick(double[] newVal){
        if (data.size() != 0){
            double[] curVal = {curX, curY, curZ};
            MovingFilter accFilter = new MovingFilter(MovingFilter.FilterType.LOW_PASS_MOD_1);
            curVal = accFilter.filter(curVal, newVal);
            curX = curVal[0];
            curY = curVal[1];
            curZ = curVal[2];
        } else {
            curX = (double)newVal[0];
            curY = (double)newVal[1];
            curZ = (double)newVal[2];
        }

        double mean = Math.sqrt(curX*curX + curY*curY + curZ*curZ);
        data.add(new AccRecord(curX, curY, curZ, mean));

        sumX += curX;
        sumY += curY;
        sumZ += curZ;
        counter++;
    }

    public void bigTick(){
        if (counter == 0){
            return;
        }
        meanX = sumX / counter;
        meanY = sumY / counter;
        meanZ = sumZ / counter;
        meanAbs = Math.sqrt(meanX*meanX + meanY*meanY + meanZ*meanZ);
        counter = 0;
        sumX = sumY = sumZ = 0.0;
        //data.add(new AccRecord(meanX, meanY, meanZ, meanAbs));
        //zeroValues();
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
    public Double getMeanAbs() {
        return meanAbs;
    }

    public Double getMeanX() {
        return meanX;
    }

    public Double getMeanY() {
        return meanY;
    }

    public Double getMeanZ() {
        return meanZ;
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

    public Integer getCounter() {
        return counter;
    }

    public Integer getBigTickDelay() {
        return bigTickDelay;
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

    public void setBigTickDelay(Integer bigTickDelay) {
        this.bigTickDelay = bigTickDelay;
    }
}
