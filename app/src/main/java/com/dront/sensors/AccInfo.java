package com.dront.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

public class AccInfo implements SensorEventListener {


    private static final int DEFAULT_SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;
    private static final int DEFAULT_ARR_SIZE = 500;
    private static final double DEFAULT_FLIGHT_GRAVITY = 5.0;
    private static final double MIN_FLIGHT_TIME = 0.2;

    private  static volatile AccInfo instance;

    private SensorManager manager;
    private Sensor sensor;

    private AccRecord curRecord;
    private ArrayList<AccRecord> data;
    private Integer delay;
    private boolean enabled;
    private String info;
    private Double resolution;
    private Double flightGravityMax;

    //private because it's a singleton
    private AccInfo(SensorManager s){
        manager = s;
        sensor = manager.getDefaultSensor(Constants.DEFAULT_SENSOR);
        manager.unregisterListener(this);
        delay = DEFAULT_SENSOR_DELAY;
        info = sensor.toString();
        resolution = (double)sensor.getResolution();
        flightGravityMax = DEFAULT_FLIGHT_GRAVITY;
        enabled = false;
        curRecord = new AccRecord();
        data = new ArrayList<AccRecord>(DEFAULT_ARR_SIZE);
    }

    //private methods
    private void zeroValues(){
        curRecord = new AccRecord();
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

    private void smallTick(float[] newVal){
        if (data.size() != 0){
            MovingFilter accFilter = new MovingFilter(MovingFilter.FilterType.LOW_PASS_MOD_1);
            curRecord = accFilter.filter(curRecord, new AccRecord(newVal));
        } else {
            curRecord = new AccRecord(newVal);
        }
        data.add(curRecord);
    }

    public double[] getFlights(){
        ArrayList<Double> intervals = findFlightIntervals();

        if (intervals.isEmpty()){
            return null;
        }
        return countHeights(intervals);
    }

    //public methods
    public static AccInfo getInstance(SensorManager s){
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

    public void enable() {
        zeroValues();
        data.clear();
        manager.registerListener(this, sensor, delay);
        this.enabled = true;
    }

    public void disable(){
        data.trimToSize();
        manager.unregisterListener(this);
        this.enabled = false;
    }

    public boolean isDataEmpty(){
        return data.isEmpty();
    }

    //getters
    public Float getLastAbs() {
        return curRecord.getAbs();
    }

    public Float getLastX() {
        return curRecord.getX();
    }

    public Float getLastY() {
        return curRecord.getY();
    }

    public Float getLastZ() {
        return curRecord.getZ();
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        smallTick(event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
