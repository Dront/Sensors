package com.dront.sensors;

public class AccRecord {
    private long time;
    public float[] values;

    //constructors
    public AccRecord(float x, float y, float z, long t){
        time = t;
        values = new float[4];
        values[0] = x;
        values[1] = y;
        values[2] = z;
        countAbs();
    }

    public AccRecord(float[] data, long t){
        time = t;
        values = new float[4];
        copy3Elements(data);
        countAbs();
    }

    public AccRecord(float x, float y, float z){
        time = System.currentTimeMillis();
        values = new float[4];
        values[0] = x;
        values[1] = y;
        values[2] = z;
        countAbs();
    }

    public AccRecord(float[] data){
        time = System.currentTimeMillis();
        values = new float[4];
        copy3Elements(data);
        countAbs();
    }

    public AccRecord(){
        time = System.currentTimeMillis();
        values = new float[4];
    }

    //private methods
    private void copy3Elements(float[] data){
        values[0] = data[0];
        values[1] = data[1];
        values[2] = data[2];
    }

    //public methods
    public void countAbs(){
        values[3] = (float)Math.sqrt(values[0]*values[0] + values[1]*values[1] + values[2]*values[2]);
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


