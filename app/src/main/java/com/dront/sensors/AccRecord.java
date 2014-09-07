package com.dront.sensors;

public class AccRecord {
    private long time;
    private float[] values;

    //constructors
    public AccRecord(float x, float y, float z, float abs, long t){
        time = t;
        values = new float[4];
        values[0] = x;
        values[1] = y;
        values[2] = z;
        values[3] = abs;
    }

    public AccRecord(float x, float y, float z, float abs){
        time = System.currentTimeMillis();
        values = new float[4];
        values[0] = x;
        values[1] = y;
        values[2] = z;
        values[3] = abs;
    }

    public AccRecord(float[] data, long t){
        time = t;
        values = new float[4];
        values = data;
    }

    //getters
    public float getX(){
        return values[0];
    }

    public float getY(){
        return values[1];
    }

    public float getZ(){
        return values[2];
    }

    public float getAbs(){
        return values[3];
    }

    public long getTime(){
        return time;
    }

    //gets 4 values
    public float[] getValues(){
        return values;
    }

    //gets 3 values
    public float[] getCoords(){
        return new float[]{values[0], values[1], values[2]};
    }

    //stuff for parcelable
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeLong(time);
//        parcel.writeDouble(xVal);
//        parcel.writeDouble(yVal);
//        parcel.writeDouble(zVal);
//        parcel.writeDouble(meanVal);
//    }
//
//    public static final Parcelable.Creator<AccRecord> CREATOR = new Parcelable.Creator<AccRecord>() {
//
//        public AccRecord createFromParcel(Parcel in) {
//            return new AccRecord(in);
//        }
//
//        public AccRecord[] newArray(int size) {
//            return new AccRecord[size];
//        }
//    };
//
//    private AccRecord(Parcel parcel) {
//        time = parcel.readLong();
//        xVal = parcel.readDouble();
//        yVal = parcel.readDouble();
//        zVal = parcel.readDouble();
//        meanVal = parcel.readDouble();
//    }

}


