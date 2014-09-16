package com.dront.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

public class AccInfo implements SensorEventListener {


    private static final int DEFAULT_SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;
    private static final int DEFAULT_ARR_SIZE = 2000;
    private static final long MAX_TIME_INTERVAL = 30;

    private SensorManager manager;
    private Sensor sensor;

    private AccRecord curRecord;
    private ArrayList<AccRecord> data;
    private Integer delay;
    private boolean enabled;
    private String info;
    private Double resolution;

    public AccInfo(SensorManager s){
        manager = s;
        sensor = manager.getDefaultSensor(Constants.DEFAULT_SENSOR);
        manager.unregisterListener(this);
        delay = DEFAULT_SENSOR_DELAY;
        info = sensor.toString();
        resolution = (double)sensor.getResolution();
        enabled = false;
        curRecord = new AccRecord();
        data = new ArrayList<AccRecord>(DEFAULT_ARR_SIZE);
    }

    //private methods
    private void zeroValues(){
        curRecord = new AccRecord();
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

    //public methods
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

    public int removeOldRecords(){
        int size = data.size();
        long timeInterval = (data.get(size - 1).getTime() - data.get(0).getTime()) / 1000;
        if (timeInterval > MAX_TIME_INTERVAL){
            ArrayList<AccRecord> tmp = new ArrayList<AccRecord>(DEFAULT_ARR_SIZE);
            for (int i = size / 2; i < size; i++){
                tmp.add(data.get(i));
            }
            data = tmp;
        }
        return data.size();
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
