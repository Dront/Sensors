package com.dront.sensors;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class AccRecord implements Parcelable {
    public long time;
    public double xVal, yVal, zVal, meanVal;

    public AccRecord(long t, double x, double y, double z, double mean){
        time = t;
        xVal = x;
        yVal = y;
        zVal = z;
        meanVal = mean;
    }

    public AccRecord(double x, double y, double z, double mean){
        time = System.currentTimeMillis();
        xVal = x;
        yVal = y;
        zVal = z;
        meanVal = mean;
    }

    //stuff for parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(time);
        parcel.writeDouble(xVal);
        parcel.writeDouble(yVal);
        parcel.writeDouble(zVal);
        parcel.writeDouble(meanVal);
    }

    public static final Parcelable.Creator<AccRecord> CREATOR = new Parcelable.Creator<AccRecord>() {

        public AccRecord createFromParcel(Parcel in) {
            return new AccRecord(in);
        }

        public AccRecord[] newArray(int size) {
            return new AccRecord[size];
        }
    };

    private AccRecord(Parcel parcel) {
        time = parcel.readLong();
        xVal = parcel.readDouble();
        yVal = parcel.readDouble();
        zVal = parcel.readDouble();
        meanVal = parcel.readDouble();
    }

}


