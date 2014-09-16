package com.dront.sensors;

import android.hardware.Sensor;

public final class Constants {

    private Constants(){
        // restrict instantiation
    }

    public static final String LOG_TAG = "com.dront.sensors.log";
    public static final String LOG_ASYNC = LOG_TAG + ".async";
    public final static String LOG_SINGLETON = LOG_TAG + ".singleton";
    public final static String LOG_FLIGHT = LOG_TAG + ".flight";
    public final static String LOG_MEMORY = LOG_TAG + ".memory";

    public final static int DEFAULT_SENSOR = Sensor.TYPE_ACCELEROMETER;
}
