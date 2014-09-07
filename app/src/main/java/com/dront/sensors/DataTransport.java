package com.dront.sensors;

import android.util.Log;

import java.util.ArrayList;

//singleton for transfering records to GraphActivity
public class DataTransport extends ArrayList<AccRecord> {

    private static volatile DataTransport instance;

    public static DataTransport getInstance(){
        if (instance == null){
            synchronized (DataTransport.class){
                if (instance == null){
                    instance = new DataTransport();
                    Log.d(Constants.LOG_SINGLETON, "create singleton");
                }
            }
        }
        return instance;
    }

}
